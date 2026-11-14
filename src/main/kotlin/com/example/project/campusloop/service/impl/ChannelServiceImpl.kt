package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.dto.DirectChannelSummary
import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.enum.ChannelType
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.repository.ChannelRepository
import com.example.project.campusloop.service.ChannelMemberService
import com.example.project.campusloop.service.ChannelService
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.GroupService
import com.example.project.campusloop.service.MessageQueryService
import com.example.project.campusloop.util.SnowflakeIdGenerator
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class ChannelServiceImpl (
    private val repository: ChannelRepository,
    private val idGenerator: SnowflakeIdGenerator,
    private val memberService: GroupMemberService,
    private val groupService: GroupService,
    private val groupMemberService: GroupMemberService,
    private val channelMemberService: ChannelMemberService,
    private val messageQueryService: MessageQueryService
) : ChannelService {

    @Transactional
    override fun createChannel(
        operatorId: UUID,
        groupId: UUID?,
        name: String?,
        type: ChannelType
    ): Channel {
        val group = if (groupId != null) {
            val foundGroup = groupService.getGroupById(groupId)
            memberService.validateMemberRole(operatorId, foundGroup, GroupMemberRole.ADMIN)
            foundGroup
        } else {
            if (type != ChannelType.DIRECT) {
                throw IllegalArgumentException("Non-direct channels must belong to a group!")
            }
            null
        }

        val channel = Channel(
            id = idGenerator.nextId(),
            name = name,
            type = type,
            group = group
        )

        return repository.save(channel)
    }

    @Transactional
    override fun editChannel(
        operatorId: UUID,
        channelId: Long,
        name: String
    ): Channel {
        val channel = getChannelById(operatorId, channelId)

        channel.group?.let { group ->
            memberService.validateMemberRole(
                userId = operatorId,
                group = group,
                requiredRole = GroupMemberRole.MODERATOR
            )
        }

        channel.name = name

        return repository.save(channel)
    }

    @Transactional
    override fun deleteChannel(operatorId: UUID, channelId: Long) {
        val channel = getChannelById(operatorId, channelId)

        channel.group?.let { group ->
            memberService.validateMemberRole(
                userId = operatorId,
                group = group,
                requiredRole = GroupMemberRole.MODERATOR
            )
        }

        repository.delete(channel)
    }

    @Transactional(readOnly = true)
    override fun getGroupChannels(
        operatorId: UUID,
        groupId: UUID,
        pageable: Pageable
    ): Slice<Channel> {
        val group = groupService.getGroupById(groupId)

        if (
            group.isPrivate &&
            !groupMemberService.isMemberActive(groupId, operatorId, GroupMemberStatus.ACTIVE
            )) {
            throw IllegalArgumentException("Channel cannot be accessed due to private setting of the group")
        }

        return repository.findAllByGroup(group, pageable)
    }

    @Transactional(readOnly = true)
    override fun getChannelById(
        operatorId: UUID,
        channelId: Long
    ): Channel {
        val channel = repository.findByIdOrNull(channelId) ?:
            throw IllegalArgumentException("Channel cannot be found!")

        when (channel.type) {
            ChannelType.DIRECT -> {
                if (!channelMemberService.isMember(channel, operatorId)) {
                    throw IllegalArgumentException("You are not a member of this direct channel!")
                }
            }
            ChannelType.GROUP_TEXT -> {
                val group = channel.group ?: throw IllegalStateException("Group channel must belong to a group")
                if (group.isPrivate && !groupMemberService.isMemberActive(group.id!!, operatorId, GroupMemberStatus.ACTIVE)) {
                    throw IllegalArgumentException("You do not have access to this group channel!")
                }
            }
        }

        return channel
    }

    @Transactional(readOnly = true)
    override fun getUserDirectChannels(
        userId: UUID,
        pageable: Pageable
    ): Slice<DirectChannelSummary> {
        val channelSlice = repository.findActiveDirectMessages(userId, ChannelType.DIRECT, pageable)
        if (channelSlice.isEmpty) {
            return SliceImpl(emptyList(), pageable, false)
        }

        val channelIds = channelSlice.mapNotNull { it.id }

        val allMembers = channelMemberService.getMembersByChannelIds(channelIds)
        val membersByChannel = allMembers.groupBy { it.channel.id!! }

        val channelWithUnread = channelSlice.content.mapNotNull { channel ->
            val channelId = channel.id ?: return@mapNotNull null
            val lastMessageId = channel.lastMessageId ?: return@mapNotNull null
            val myMember = membersByChannel[channelId]?.firstOrNull { it.user.id == userId }
            val myLastReadId = myMember?.lastReadMessageId ?: 0L

            if (myLastReadId < lastMessageId) channelId else null
        }

        val unreadCountByChanelId = messageQueryService.getUnreadCountsByChannelIds(userId, channelIds)

        val summery = channelSlice.mapNotNull { channel ->
            val channelId = channel.id ?: return@mapNotNull null
            val members = membersByChannel[channelId].orEmpty()
            val counterpart = members.firstOrNull { it.user.id != userId }?.user
                ?: return@mapNotNull null

            DirectChannelSummary(
                channel = channel,
                counterpart = counterpart,
                unreadCount = unreadCountByChanelId.get(channelId) ?: 0
            )
        }

        return SliceImpl(summery, pageable, channelSlice.hasNext())
    }

    @Transactional
    override fun onNewMessage(
        channelId: Long,
        messageId: Long,
        senderId: UUID,
        rawContent: String?,
        createdAt: Instant
    ) {
        val preview = formatPreview(rawContent)

        repository.updateLastMessageMetadata(
            channelId = channelId,
            messageId = messageId,
            contentPreview = preview,
            senderId = senderId,
            activityAt = createdAt
        )

        channelMemberService.updateLastReadMessage(channelId = channelId, userId = senderId, messageId = messageId)
    }

    @Transactional
    override fun onMessageDelete(channelId: Long, deletedMessageId: Long) {
        val channel = repository.findByIdOrNull(channelId) ?:
            throw IllegalArgumentException("Channel with id: ${channelId} cannot be found!")
        if (channel.lastMessageId != deletedMessageId) return

        val previousMessage = messageQueryService.getLatestMessage(channelId)
        if (previousMessage != null) {
            channel.lastMessageId = previousMessage.id
            channel.lastMessageContent = formatPreview(previousMessage.content)
            channel.lastSenderId = previousMessage.sender.id
            channel.lastActivityAt = previousMessage.createdAt
        } else {
            channel.lastMessageId = null
            channel.lastMessageContent = null
            channel.lastSenderId = null
            channel.lastActivityAt = channel.createdAt
        }
    }

    private fun formatPreview(rawContent: String?): String {
        return rawContent
            ?.trim()
            ?.take(100)
            ?.ifBlank { null }
            ?: "Attachment"
    }
}
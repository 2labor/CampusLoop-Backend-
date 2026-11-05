package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.enum.ChannelType
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.repository.ChannelRepository
import com.example.project.campusloop.service.ChannelMemberService
import com.example.project.campusloop.service.ChannelService
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.GroupService
import com.example.project.campusloop.util.SnowflakeIdGenerator
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChannelServiceImpl (
    private val repository: ChannelRepository,
    private val idGenerator: SnowflakeIdGenerator,
    private val memberService: GroupMemberService,
    private val groupService: GroupService,
    private val groupMemberService: GroupMemberService,
    private val channelMemberService: ChannelMemberService,
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

}
package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.ChannelMember
import com.example.project.campusloop.domain.enum.ChannelType
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.repository.ChannelMemberRepository
import com.example.project.campusloop.repository.ChannelRepository
import com.example.project.campusloop.service.ChannelMemberService
import com.example.project.campusloop.service.ChannelService
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.UserService
import com.example.project.campusloop.util.SnowflakeIdGenerator
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChannelMemberServiceImpl (
    private val repository: ChannelMemberRepository,
    private val channelRepository: ChannelRepository,
    private val userService: UserService,
    private val idGenerator: SnowflakeIdGenerator,
    private val groupMemberService: GroupMemberService,
) : ChannelMemberService {

    @Transactional
    override fun getOrCreateDirectChannel(
        operatorId: UUID,
        recipientId: UUID
    ): Channel {
        if (operatorId == recipientId) {
            throw IllegalArgumentException("You cannot create chat with yourself")
        }

        val user1 = userService.getUserById(operatorId) ?:
        throw IllegalArgumentException("No user found with UUID $operatorId")
        val user2 = userService.getUserById(recipientId)
            ?: throw IllegalArgumentException("No user found with UUID $recipientId")

        val existingChannel = channelRepository.findDirectChannelBetweenUsers(operatorId, recipientId)
        if (existingChannel != null) {
            return existingChannel
        }

        val newChannel = Channel(
            id = idGenerator.nextId(),
            name = null,
            type = ChannelType.DIRECT,
            group = null
        )

        val channel = channelRepository.save(newChannel)

        val member1 = ChannelMember(
            id = idGenerator.nextId(),
            user = user1,
            channel = channel
        )
        val member2 = ChannelMember(
            id = idGenerator.nextId(),
            user = user2,
            channel = channel
        )

        repository.saveAll(listOf(member1, member2))

        return channel
    }

    @Transactional
    override fun addMember(
        operatorId: UUID,
        channelId: Long,
        userId: UUID
    ): ChannelMember {
        val channel = channelRepository.findByIdOrNull(channelId)
            ?: throw IllegalArgumentException("No channel found with id $channelId")
        if (channel.type == ChannelType.DIRECT) {
            throw IllegalArgumentException("You cannot add direct channel")
        }

        if (isMember(channel, userId)) {
            throw IllegalArgumentException("User already exists in channel")
        }

        val group = channel.group!!
        if (group.isPrivate) {
            groupMemberService.validateMemberRole(group = group, userId = operatorId, requiredRole = GroupMemberRole.MODERATOR)
        }

        val user = userService.getUserById(userId)
            ?: throw IllegalArgumentException("No user found with UUID $userId")

        val member = ChannelMember(
            id = idGenerator.nextId(),
            user = user,
            channel = channel
        )

        return repository.save(member)
    }

    @Transactional
    override fun removeMember(operatorId: UUID, channelId: Long, memberId: Long) {
        val channel = channelRepository.findByIdOrNull(channelId)
            ?: throw IllegalArgumentException("No channel found with id $channelId")
        if (channel.type == ChannelType.DIRECT) {
            throw IllegalArgumentException("You cannot remove member from direct channel")
        }

        val group = channel.group!!
        if (group.isPrivate) {
            groupMemberService.validateMemberRole(group = group, userId = operatorId, requiredRole = GroupMemberRole.MODERATOR)
        }

        val member = repository.findByIdOrNull(memberId)
            ?: throw IllegalArgumentException("No member found with id $memberId")

        repository.delete(member)
    }

    @Transactional(readOnly = true)
    override fun getAllMembers(
        operatorId: UUID,
        channelId: Long,
        pageable: Pageable
    ): Slice<ChannelMember> {
        val channel = channelRepository.findByIdOrNull(channelId)
            ?: throw IllegalArgumentException("No channel found with id $channelId")
        return repository.findAllByChannel(channel, pageable)
    }

    @Transactional(readOnly = true)
    override fun getUsersChannels(
        operatorId: UUID,
        pageable: Pageable
    ): Slice<Channel> {
        val user = userService.getUserById(operatorId)
            ?: throw IllegalArgumentException("No user found with UUID $operatorId")

        return repository.findAllByUser(user, pageable).map { it.channel }
    }

    @Transactional(readOnly = true)
    override fun isMember(channel: Channel, userId: UUID): Boolean {
        val user = userService.getUserById(userId) ?:
        throw IllegalArgumentException("No user found with UUID $userId")

        return repository.existsByChannelAndUser(channel, user)
    }
}
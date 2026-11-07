package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.CreateMessageCommand
import com.example.project.campusloop.domain.command.EditMessageCommand
import com.example.project.campusloop.domain.entity.Message
import com.example.project.campusloop.domain.enum.ChannelType
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.HistoryDirection
import com.example.project.campusloop.repository.MessageRepository
import com.example.project.campusloop.service.ChannelMemberService
import com.example.project.campusloop.service.ChannelService
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.MessageService
import com.example.project.campusloop.service.UserService
import com.example.project.campusloop.util.SnowflakeIdGenerator
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class MessageServiceImpl (
    private val repository: MessageRepository,
    private val channelService: ChannelService,
    private val memberService: ChannelMemberService,
    private val idGenerator: SnowflakeIdGenerator,
    private val userService: UserService,
    private val groupMemberService: GroupMemberService,
) : MessageService {

    @Transactional
    override fun sendMessage(
        authorId: UUID,
        channelId: Long,
        command: CreateMessageCommand
    ): Message {
        val author = userService.getUserById(authorId)
            ?: throw IllegalArgumentException("User does with id: $authorId not exist")

        val channel = channelService.getChannelById(authorId, channelId)

        if (!memberService.isMember(channel, authorId)) {
            throw IllegalArgumentException("You have to be member of a channel to send a message!")
        }

        val parentMessage = command.parentMessageId?.let { pId ->
            val pMsg = repository.findByIdOrNull(pId)
                ?: throw IllegalArgumentException("Message with id: $pId does not exist!")

            if (pMsg.channel.id != channelId) {
                throw IllegalArgumentException("Cannot reply to a message from another channel!")
            }

            pMsg
        }

        val message = Message(
            id = idGenerator.nextId(),
            content = command.content,
            sender = author,
            channel = channel,
            parentMessage = parentMessage,
        )

        return repository.save(message)
    }

    @Transactional
    override fun editMessage(
        operatorId: UUID,
        messageId: Long,
        command: EditMessageCommand
    ): Message {
        val message = repository.findByIdOrNull(messageId) ?:
            throw IllegalArgumentException("Message with id: $messageId not exist!")

        if (!memberService.isMember(message.channel, operatorId)) {
            throw IllegalArgumentException("You have to be member of a channel to edit")
        }

        if (message.sender.id != operatorId) {
            throw IllegalArgumentException("You cannot modify a message that doesn't belong to you")
        }

        message.content = command.content

        return repository.save(message)
    }

    @Transactional
    override fun deleteMessage(operatorId: UUID, messageId: Long) {
        val message = repository.findByIdOrNull(messageId)
            ?: throw IllegalArgumentException("Message with id: $messageId not found!")

        if (!memberService.isMember(message.channel, operatorId)) {
            throw IllegalArgumentException("You have to be member of a channel to delete a message!")
        }

        val channelType = message.channel.type
        when (channelType) {
            ChannelType.DIRECT -> {
                if (message.sender.id != operatorId) {
                    throw IllegalArgumentException("You cannot delete a message that doesn't belong to you")
                }
            }
            ChannelType.GROUP_TEXT -> {
                if (message.sender.id != operatorId) {
                    groupMemberService.validateMemberRole(operatorId, message.channel.group!!, GroupMemberRole.MODERATOR)
                }
            }
        }

        repository.delete(message)
    }

    @Transactional(readOnly = true)
    override fun getMessageById(
        operatorId: UUID,
        messageId: Long
    ): Message {
        val message = repository.findByIdOrNull(messageId)
            ?: throw IllegalArgumentException("Message with id: $messageId not found!")

        if(!memberService.isMember(message.channel, operatorId)) {
            throw IllegalArgumentException("You have to be member of a channel to get a message!")
        }

        return message
    }

    @Transactional(readOnly = true)
    override fun getLastMessage(
        operatorId: UUID,
        channelId: Long
    ): Message? {
        val lastMessage = repository.findFirstByChannelIdOrderByIdDesc(channelId)

        if (lastMessage != null && memberService.isMember(lastMessage.channel, operatorId)) {
            throw IllegalArgumentException("You have to be member of a channel to get a message!")
        }

        return lastMessage
    }

    @Transactional(readOnly = true)
    override fun getChannelHistory(
        operatorId: UUID,
        channelId: Long,
        cursorId: Long?,
        direction: HistoryDirection,
        pageable: Pageable
    ): Slice<Message> {
        channelService.getChannelById(operatorId, channelId)

        if (cursorId == null) {
            return repository.findAllByChannelIdOrderByIdDesc(channelId, pageable)
        }

        return when (direction) {
            HistoryDirection.BEFORE -> repository.findAllByChannelIdAndIdLessThanOrderByIdDesc(
                channelId,
                cursorId,
                pageable
            )
            HistoryDirection.AFTER -> repository.findAllByChannelIdAndIdGreaterThanOrderByIdAsc(
                channelId,
                cursorId,
                pageable
            )
        }
    }
}
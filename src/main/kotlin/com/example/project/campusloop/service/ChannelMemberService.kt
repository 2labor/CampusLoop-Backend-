package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.ChannelMember
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface ChannelMemberService {
    fun addMember(operatorId: UUID, channelId: Long, userId: UUID): ChannelMember
    fun removeMember(operatorId: UUID, channelId: Long, memberId: Long)
    fun getAllMembers(operatorId: UUID, channelId: Long, pageable: Pageable): Slice<ChannelMember>
    fun getUsersChannels(operatorId: UUID, pageable: Pageable): Slice<Channel>
    fun getOrCreateDirectChannel(operatorId: UUID, recipientId: UUID): Channel
    fun isMember(channel: Channel, userId: UUID): Boolean
}
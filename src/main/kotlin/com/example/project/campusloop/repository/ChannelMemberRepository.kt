package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.ChannelMember
import com.example.project.campusloop.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository


interface ChannelMemberRepository : JpaRepository<ChannelMember, Long> {
    fun findAllByUser(user: User, pageable: Pageable): Slice<ChannelMember>
    fun findByChannelAndUser(channel: Channel, user: User): ChannelMember?
    fun existsByChannelAndUser(channel: Channel, user: User): Boolean
    fun findAllByChannel(channel: Channel, pageable: Pageable): Slice<ChannelMember>
}
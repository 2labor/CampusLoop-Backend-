package com.example.project.campusloop.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "channel_members")
class ChannelMember (
    @Id
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    val channel: Channel,

    @Column(name = "last_read_message_id")
    var lastReadMessageId: Long? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }
}
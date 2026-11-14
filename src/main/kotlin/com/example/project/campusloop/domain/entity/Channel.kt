package com.example.project.campusloop.domain.entity

import com.example.project.campusloop.domain.enum.ChannelType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "channels",
    indexes = [
        Index(name = "idx_channels_type_last_activity", columnList = "channel_type, last_activity_at DESC"
    )])
class Channel(
    @Id
    val id: Long? = null,

    @Column(name = "name")
    var name: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    var type: ChannelType,

    @Column(name = "last_message_id")
    var lastMessageId: Long? = null,

    @Column(name = "last_message_content")
    var lastMessageContent: String? = null,

    @Column(name = "last_message_sender")
    var lastSenderId: UUID? = null,

    @Column(name = "last_activity_at", nullable = false)
    var lastActivityAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    val group: Group? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
package com.example.project.campusloop.domain.entity

import com.example.project.campusloop.domain.enum.NotificationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "notifications")
class Notification (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "receiver_id", nullable = false)
    val receiverId: UUID,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "message")
    val message: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    val type: NotificationType,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "create_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
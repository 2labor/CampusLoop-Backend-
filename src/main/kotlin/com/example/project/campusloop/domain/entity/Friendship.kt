package com.example.project.campusloop.domain.entity

import com.example.project.campusloop.domain.enum.FriendshipStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
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
    name = "friendships",
    indexes = [
        Index(
        name = "idx_friendship_user_target",
        columnList = "initializer_id, target_id",
        unique = true
    ),
        Index(
            name = "idx_friendship_target_status",
            columnList = "target_id, status"
        )

    ]
)
class Friendship (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @JoinColumn(name = "initializer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    val initializer: User,

    @JoinColumn(name = "target_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    val target: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: FriendshipStatus,

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
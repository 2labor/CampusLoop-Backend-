package com.example.project.campusloop.domain.entity

import com.example.project.campusloop.domain.enum.ChannelType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "channels")
class Channel(
    @Id
    val id: Long? = null,

    @Column(name = "name")
    var name: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    var type: ChannelType,

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
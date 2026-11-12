package com.example.project.campusloop.security.domain.entity

import com.example.project.campusloop.domain.enum.TokenType
import jakarta.persistence.PreUpdate
import java.time.Instant
import java.util.UUID

data class Token (
    val id: UUID = UUID.randomUUID(),
    val identifier: String,
    val tokenStr: String,
    val type: TokenType,
    val expiresIn: Instant,
    val targetEntityId: String? = null,
    val maxUses: Int? = null,
    val currentUses: Int = 0,
    val createAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
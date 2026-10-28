package com.example.project.campusloop.security.token

import com.example.project.campusloop.domain.enum.TokenType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

data class Token (
    val id: UUID = UUID.randomUUID(),
    val identifier: String,
    val tokenStr: String,
    val type: TokenType,
    val expiresIn: Instant,
    val targetEntityId: UUID? = null,
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
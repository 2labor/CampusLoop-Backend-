package com.example.project.campusloop.security.domain.dto

import com.example.project.campusloop.domain.enum.TokenType
import java.time.Instant

data class ActionTokenResponse(
    val token: String,
    val type: TokenType,
    val targetEntityId: String,
    val expiresAt: Instant,
    val maxUses: Int? = null
)

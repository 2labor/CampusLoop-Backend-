package com.example.project.campusloop.security.domain.entity

import com.example.project.campusloop.domain.enum.TokenType
import java.time.Instant

data class ShareableToken(
    val rawToken: String,
    val type: TokenType,
    val targetEntityId: String,
    val expiresIn: Instant,
    val maxUses: Int? = null,
    val currUse: Int = 0,
)

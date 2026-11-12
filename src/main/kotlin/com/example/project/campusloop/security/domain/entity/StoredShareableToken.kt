package com.example.project.campusloop.security.domain.entity

import com.example.project.campusloop.domain.enum.TokenType
import java.time.Instant

data class StoredShareableToken(
    val type: TokenType,
    val targetEntityId: String,
    val expiredIn: Instant,
    val maxUses: Int? = null,
    val currentUses: Int? = 0,
)

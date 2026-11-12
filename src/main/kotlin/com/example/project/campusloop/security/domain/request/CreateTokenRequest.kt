package com.example.project.campusloop.security.domain.request

import com.example.project.campusloop.domain.enum.TokenType

data class CreateTokenRequest(
    val type: TokenType,
    val targetEntityId: String,
    val durationInMinutes: Long = 1440,
    val maxUses: Int? = null
)

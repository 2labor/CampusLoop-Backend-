package com.example.project.campusloop.security.token.service

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.Token

interface ShareableTokenService {
    fun createToken(
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long = 1440, // 24h
        maxUses: Int? = null
    ): Token
    fun consumeToken(
        tokenValue: String,
        expectedType: TokenType,
    ): Token?
}
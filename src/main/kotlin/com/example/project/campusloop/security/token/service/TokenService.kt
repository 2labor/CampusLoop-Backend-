package com.example.project.campusloop.security.token.service

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.Token
import java.util.UUID

interface TokenService {
    fun createToken(identifier: String, tokenStr: String, tokenType: TokenType, durationMinutes: Long, targetEntityId: UUID? = null, maxUses: Int? = null): Token
    fun validateAndConsume(identifier: String, tokenValue: String, type: TokenType): Token?
}
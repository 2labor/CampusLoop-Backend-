package com.example.project.campusloop.security.token.service

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.ShareableToken
import java.util.UUID

interface ShareableTargetHandler<T> {
    fun getSupportedType(): Set<TokenType>
    fun createToken(
        operatorId: UUID,
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long = 1440,
        maxUses: Int? = null
    ): ShareableToken
    fun processToken(type: TokenType, operatorId: UUID, targetEntityId: String)
}
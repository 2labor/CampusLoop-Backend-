package com.example.project.campusloop.security.token.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.Token
import com.example.project.campusloop.security.token.service.TokenService
import com.example.project.campusloop.security.token.service.ShareableTokenService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ShareableTokenServiceImpl(
    private val tokenService: TokenService
) : ShareableTokenService {
    override fun createToken(
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long,
        maxUses: Int?
    ): Token {
        val tokenRow = UUID.randomUUID().toString()

        val token = tokenService.createToken(
            identifier = tokenRow,
            tokenType = type,
            tokenStr = tokenRow,
            durationMinutes = durationInMinutes,
            targetEntityId = targetEntityId,
            maxUses = maxUses
        )

        return token
    }

    override fun consumeToken(
        tokenValue: String,
        expectedType: TokenType
    ): Token? {
        return tokenService.validateAndConsume(tokenValue, tokenValue, expectedType)
    }
}
package com.example.project.campusloop.security.token.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.ShareableToken
import com.example.project.campusloop.security.token.service.ShareableTargetHandler
import com.example.project.campusloop.security.token.service.ShareableTokenService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ShareableTokenFacadeImpl(
    private val tokenService: ShareableTokenService,
    handleList: List<ShareableTargetHandler<*>>
) {

    private val handlers: Map<TokenType, ShareableTargetHandler<*>> = buildMap {
        handleList.forEach { handler ->
            handler.getSupportedType().forEach { type ->
                put(type, handler)
            }
        }
    }

    fun createToken(
        operatorId: UUID,
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long = 1440,
        maxUses: Int? = null
    ): ShareableToken {
        val handler = getHandlerOrThrow(type)
        return handler.createToken(operatorId, type, targetEntityId, durationInMinutes, maxUses)
    }

    fun consumeAndProcess(
        currentUserId: UUID,
        tokenValue: String,
        type: TokenType
    ) {
        val token = tokenService.consumeToken(tokenValue, type)
            ?: throw IllegalArgumentException("tokenValue is invalid or expired")

        val handler = getHandlerOrThrow(type)

        handler.processToken(type = type, operatorId = currentUserId, targetEntityId = token.targetEntityId ?: "")
    }

    private fun getHandlerOrThrow(type: TokenType): ShareableTargetHandler<*> {
        return handlers[type]
            ?: throw IllegalArgumentException("No handler for type $type")
    }
}
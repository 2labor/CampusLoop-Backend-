package com.example.project.campusloop.security.domain.mapper.impl

import com.example.project.campusloop.security.domain.dto.ActionTokenResponse
import com.example.project.campusloop.security.domain.entity.ShareableToken
import com.example.project.campusloop.security.domain.mapper.TokenMapper
import com.example.project.campusloop.security.domain.entity.Token
import org.springframework.stereotype.Component

@Component
class TokenMapperImpl : TokenMapper {
    override fun toResponse(token: ShareableToken): ActionTokenResponse {
        return ActionTokenResponse(
            token = token.rawToken,
            type = token.type,
            targetEntityId = token.targetEntityId,
            expiresAt = token.expiresIn,
            maxUses = token.maxUses
        )
    }
}
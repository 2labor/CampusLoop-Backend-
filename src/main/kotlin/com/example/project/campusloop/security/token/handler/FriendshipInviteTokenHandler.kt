package com.example.project.campusloop.security.token.handler

import com.example.project.campusloop.domain.entity.Friendship
import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.Token
import com.example.project.campusloop.security.token.service.ShareableTargetHandler
import com.example.project.campusloop.security.token.service.ShareableTokenService
import com.example.project.campusloop.service.FriendshipService
import com.example.project.campusloop.service.UserService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FriendshipInviteTokenHandler(
    private val tokenService: ShareableTokenService,
    private val service: FriendshipService,
    private val userService: UserService
)
    : ShareableTargetHandler<Friendship> {
    override fun getSupportedType(): Set<TokenType> {
        return setOf(
            TokenType.ADD_FRIEND
        )
    }

    override fun createToken(
        operatorId: UUID,
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long,
        maxUses: Int?
    ): Token {
        val userId = getId(targetEntityId)

        if (!userService.existById(userId)) {
            throw IllegalArgumentException("User with id $userId does not exists")
        }

        if (userId != operatorId) {
            throw IllegalArgumentException("You cannot create token for user id different with yours")
        }

        return tokenService.createToken(
            type = type,
            targetEntityId = targetEntityId,
            durationInMinutes = durationInMinutes,
            maxUses = maxUses
        )
    }

    override fun processToken(
        type: TokenType,
        operatorId: UUID,
        targetEntityId: String
    ) {
        val targetUserId = getId(targetEntityId)
        when(type) {
            TokenType.ADD_FRIEND -> service.createFriendship(targetUserId, operatorId)
            else -> throw IllegalArgumentException("Type $type is not supported by this service")
        }
    }

    private fun getId(entityId: String): UUID {
        return UUID.fromString(entityId)
    }
}
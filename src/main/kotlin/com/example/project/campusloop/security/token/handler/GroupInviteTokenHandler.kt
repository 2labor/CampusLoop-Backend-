package com.example.project.campusloop.security.token.handler

import com.example.project.campusloop.domain.entity.GroupMember
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.Token
import com.example.project.campusloop.security.token.service.ShareableTargetHandler
import com.example.project.campusloop.security.token.service.ShareableTokenService
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.GroupService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class GroupInviteTokenHandler (
    private val service: GroupMemberService,
    private val groupService: GroupService,
    private val tokenService: ShareableTokenService
) : ShareableTargetHandler<GroupMember> {
    override fun getSupportedType(): Set<TokenType> {
        return setOf(
            TokenType.JOIN_GROUP
        )
    }

    override fun createToken(
        operatorId: UUID,
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long,
        maxUses: Int?
    ): Token {
        val group = groupService.getGroupById(getId(targetEntityId))
        service.validateMemberRole(operatorId, group, GroupMemberRole.MODERATOR)

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
        val groupId = getId(targetEntityId)
        when(type) {
            TokenType.JOIN_GROUP -> service.addMember(operatorId, groupId)
            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    private fun getId(entityId: String): UUID {
        return UUID.fromString(entityId)
    }
}
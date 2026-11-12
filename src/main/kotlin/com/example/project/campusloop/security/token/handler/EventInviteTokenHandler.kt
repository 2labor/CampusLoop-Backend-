package com.example.project.campusloop.security.token.handler

import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.ShareableToken
import com.example.project.campusloop.security.token.service.ShareableTargetHandler
import com.example.project.campusloop.security.token.service.ShareableTokenService
import com.example.project.campusloop.service.EventParticipantService
import com.example.project.campusloop.service.EventService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EventInviteTokenHandler(
    private val service: EventParticipantService,
    private val eventService: EventService,
    private val toknService: ShareableTokenService
) : ShareableTargetHandler<EventParticipant> {
    override fun getSupportedType(): Set<TokenType> {
        return setOf(TokenType.JOIN_EVENT)
    }

    override fun createToken(
        operatorId: UUID,
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long,
        maxUses: Int?
    ): ShareableToken {
        val eventId = getId(targetEntityId)
        val event = eventService.getEventDetails(eventId)
        if (!eventService.canAddToEvent(operatorId, event)) {
            throw IllegalArgumentException("Not able to add event to user")
        }

        return toknService.createToken(
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
        val eventId = getId(targetEntityId)
        when(type) {
            TokenType.JOIN_EVENT -> {
                service.addToEventParticipant(operatorId, eventId,)
            }
            else -> throw IllegalArgumentException("Type $type is not supported")
        }
    }

    private fun getId(entityId: String): UUID {
        return UUID.fromString(entityId)
    }
}
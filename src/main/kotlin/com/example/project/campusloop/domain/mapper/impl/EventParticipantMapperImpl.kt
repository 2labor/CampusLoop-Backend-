package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.dto.ParticipantResponse
import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.mapper.EventParticipantMapper
import org.springframework.stereotype.Component

@Component
class EventParticipantMapperImpl : EventParticipantMapper {
    override fun toResponse(participant: EventParticipant): ParticipantResponse {
        return ParticipantResponse(
            id = participant.id ?: throw IllegalStateException("Participant ID cannot be null"),
            userId = participant.user.id ?: throw IllegalStateException("Account ID cannot be null"),
            username = participant.user.username,
            status = participant.status,
            joinedAt = participant.joinedAt
        )
    }
}
package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.dto.ParticipantResponse
import com.example.project.campusloop.domain.entity.EventParticipant

interface EventParticipantMapper {
    fun toResponse(participant: EventParticipant): ParticipantResponse
}
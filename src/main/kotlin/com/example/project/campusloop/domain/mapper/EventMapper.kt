package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.CreateEventCommand
import com.example.project.campusloop.domain.command.UpdateEventCommand
import com.example.project.campusloop.domain.dto.EventResponse
import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.request.CreateEventRequest
import com.example.project.campusloop.domain.request.UpdateEventRequest

interface EventMapper {
    fun toCommand(request: CreateEventRequest): CreateEventCommand
    fun toCommand(request: UpdateEventRequest): UpdateEventCommand
    fun toResponse(event: Event): EventResponse
}
package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.CreateMessageCommand
import com.example.project.campusloop.domain.command.EditMessageCommand
import com.example.project.campusloop.domain.dto.LastMessageResponse
import com.example.project.campusloop.domain.dto.MessageResponse
import com.example.project.campusloop.domain.entity.Message
import com.example.project.campusloop.domain.request.CreateMessageRequest
import com.example.project.campusloop.domain.request.EditMessageRequest

interface MessageMapper {
    fun toResponse(entity: Message): MessageResponse
    fun toLastMessageResponse(entity: Message): LastMessageResponse
    fun toCommand(request: CreateMessageRequest): CreateMessageCommand
    fun toCommand(request: EditMessageRequest): EditMessageCommand
}
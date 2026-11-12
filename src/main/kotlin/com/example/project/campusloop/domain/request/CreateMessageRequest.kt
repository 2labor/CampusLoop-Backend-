package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.command.CreateAttachmentCommand
import java.util.UUID

data class CreateMessageRequest(
    val content: String,
    val parentMessageId: Long? = null,
    val attachments: List<CreateAttachmentCommand> = emptyList()
)

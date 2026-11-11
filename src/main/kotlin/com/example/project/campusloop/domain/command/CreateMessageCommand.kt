package com.example.project.campusloop.domain.command

import java.util.UUID

data class CreateMessageCommand(
    val content: String,
    val parentMessageId: Long? = null,
    val attachments: List<CreateAttachmentCommand> = emptyList()
)

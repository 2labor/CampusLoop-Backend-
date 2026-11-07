package com.example.project.campusloop.domain.command

data class CreateMessageCommand(
    val content: String,
    val parentMessageId: Long? = null
)

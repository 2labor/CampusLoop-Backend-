package com.example.project.campusloop.domain.request

data class CreateMessageRequest(
    val content: String,
    val parentMessageId: Long? = null
)

package com.example.project.campusloop.domain.dto

import java.time.Instant

data class LastMessageResponse(
    val id: Long,
    val content: String,
    val senderName: String,
    val createdAt: Instant
)
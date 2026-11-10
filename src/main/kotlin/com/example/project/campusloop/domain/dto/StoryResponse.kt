package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.StoriesPrivacy
import java.time.Instant
import java.util.UUID

data class StoryResponse(
    val id: UUID,
    val mediaUrl: String,
    val caption: String? = null,
    val authorId: UUID,
    val authorName: String,
    val privacySetting: StoriesPrivacy,
    val createdAt: Instant = Instant.now(),
)

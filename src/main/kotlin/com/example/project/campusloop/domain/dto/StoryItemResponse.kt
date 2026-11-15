package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.StoriesPrivacy
import java.time.Instant
import java.util.UUID

data class StoryItemResponse(
    val id: UUID,
    val mediaFileKey: String,
    val caption: String?,
    val privacy: StoriesPrivacy,
    val createdAt: Instant,
    val isViewed: Boolean
)

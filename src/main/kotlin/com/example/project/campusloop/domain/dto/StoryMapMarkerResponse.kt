package com.example.project.campusloop.domain.dto

import java.time.Instant
import java.util.UUID

data class StoryMapMarkerResponse (
    val storyId: UUID,
    val authorId: UUID,
    val authorName: String,
    val latitude: Double,
    val longitude: Double,
    val mediaUrl: String,
    val caption: String?,
    val createdAt: Instant
)

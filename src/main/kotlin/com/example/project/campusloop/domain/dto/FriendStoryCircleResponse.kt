package com.example.project.campusloop.domain.dto

import java.util.UUID

data class FriendStoryCircleResponse(
    val userId: UUID,
    val name: String,
    val hasUnseen: Boolean,
    val stories: List<StoryItemResponse>,
)

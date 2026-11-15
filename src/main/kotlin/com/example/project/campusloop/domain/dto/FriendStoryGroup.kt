package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.Story
import com.example.project.campusloop.domain.entity.User
import java.time.Instant
import java.util.UUID

data class FriendStoryGroup(
    val author: User,
    val stories: List<Story>,
    val lastViewedAt: Instant?,
    val hasUnseen: Boolean
)

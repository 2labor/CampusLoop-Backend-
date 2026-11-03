package com.example.project.campusloop.domain.dto

import java.util.UUID
import com.example.project.campusloop.domain.entity.Post
import java.time.Instant

data class PostResponse(
    val id: UUID,
    val title: String?,
    val content: String,
    val groupId: UUID,
    val isAnonymous: Boolean,
    val author: AuthorDto?,
    val createdAt: Instant,
    val updatedAt: Instant
)
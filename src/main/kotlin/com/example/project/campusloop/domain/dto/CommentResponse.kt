package com.example.project.campusloop.domain.dto

import java.time.Instant
import java.util.UUID

data class CommentResponse(
    val id: UUID,
    val content: String,
    val parentId: UUID?,
    val author: AuthorDto,
    val postId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
)

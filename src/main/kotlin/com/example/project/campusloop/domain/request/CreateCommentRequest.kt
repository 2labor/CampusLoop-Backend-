package com.example.project.campusloop.domain.request

import java.util.UUID

data class CreateCommentRequest(
    val content: String,
    val parentId: UUID? = null
)

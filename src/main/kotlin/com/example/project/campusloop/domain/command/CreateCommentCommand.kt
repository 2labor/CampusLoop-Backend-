package com.example.project.campusloop.domain.command

import java.util.UUID

data class CreateCommentCommand(
    val content: String,
    val parentId: UUID? = null
)

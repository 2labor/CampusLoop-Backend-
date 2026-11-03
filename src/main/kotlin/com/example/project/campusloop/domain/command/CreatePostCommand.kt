package com.example.project.campusloop.domain.command

import java.util.UUID

data class CreatePostCommand(
    val content: String,
    val title: String? = null,
    val isAnonymous:Boolean = true
)
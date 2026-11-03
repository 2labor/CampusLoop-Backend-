package com.example.project.campusloop.domain.command

data class EditPostCommand (
    val title: String? = null,
    val content: String? = null,
    val isAnonymous:Boolean = true,
)
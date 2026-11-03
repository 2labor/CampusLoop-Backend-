package com.example.project.campusloop.domain.request

data class CreatePostRequest(
    val title: String?,
    val content: String,
    val isAnonymous: Boolean = false
)

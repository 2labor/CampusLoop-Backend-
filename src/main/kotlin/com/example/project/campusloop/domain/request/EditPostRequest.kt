package com.example.project.campusloop.domain.request

data class EditPostRequest(
    val title: String?,
    val content: String?,
    val isAnonymous: Boolean
)

package com.example.project.campusloop.domain.request

data class CreateGroupRequest(
    val name: String,
    val description: String?,
    val isPrivate: Boolean = false
)

package com.example.project.campusloop.domain.request

data class UpdateGroupRequest(
    val name: String?,
    val description: String?,
    val isPrivate: Boolean?
)

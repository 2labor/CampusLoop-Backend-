package com.example.project.campusloop.domain.command

import com.example.project.campusloop.domain.enum.Interest

data class UpdateProfileCommand(
    val avatarUrl: String?,
    val fullName: String,
    val bio: String?,
    val course: String?,
    val academicYear: Int?,
    val campus: String?,
    val interests: Set<Interest>?
)

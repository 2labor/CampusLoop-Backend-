package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.enum.Interest

data class UpdateProfileRequest(
    val fullName: String,
    val avatarUrl: String?,
    val bio: String?,
    val course: String?,
    val academicYear: Int?,
    val campus: String?,
    val interests: Set<Interest>?
)

package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.Interest
import java.util.UUID

data class UserProfileResponse(
    val userId: UUID,
    val username: String,
    val fullName: String,
    val avatarUrl: String?,
    val bio: String?,
    val course: String?,
    val academicYear: Int?,
    val campus: String?,
    val interests: Set<Interest>
)

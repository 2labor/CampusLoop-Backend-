package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.enum.Interest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:NotBlank(message = "Full name must not be blank")
    @field:Size(min = 1, max = 100, message = "Full name must be between 1 and 100 characters")
    val fullName: String,

    @field:Size(max = 512, message = "Avatar URL must not exceed 512 characters")
    val avatarUrl: String?,

    @field:Size(max = 500, message = "Bio must not exceed 500 characters")
    val bio: String?,

    @field:Size(max = 120, message = "Course must not exceed 120 characters")
    val course: String?,

    @field:Min(value = 1, message = "Academic year must be at least 1")
    @field:Max(value = 10, message = "Academic year must not exceed 10")
    val academicYear: Int?,

    @field:Size(max = 120, message = "Campus must not exceed 120 characters")
    val campus: String?,

    @field:Size(max = 20, message = "Cannot select more than 20 interests")
    val interests: Set<Interest>?
)

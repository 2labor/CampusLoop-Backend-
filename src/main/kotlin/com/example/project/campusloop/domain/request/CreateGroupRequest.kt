package com.example.project.campusloop.domain.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateGroupRequest(
    @field:NotBlank(message = "Group name must not be blank")
    @field:Size(min = 2, max = 80, message = "Group name must be between 2 and 80 characters")
    val name: String,

    @field:Size(max = 1000, message = "Group description must not exceed 1000 characters")
    val description: String?,

    val isPrivate: Boolean = false
)

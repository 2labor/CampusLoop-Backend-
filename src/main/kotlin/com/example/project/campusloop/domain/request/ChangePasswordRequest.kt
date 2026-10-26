package com.example.project.campusloop.domain.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordRequest(
    @field:NotBlank(message = "Old password cannot be blank")
    val oldPassword: String,

    @field:NotBlank(message = "New password cannot be blank")
    @field:Size(min = 6, max = 36, message = "Password must be in range (6-32) characters")
    val newPassword: String,

    @field:NotBlank(message = "Confirm password field cannot be blank")
    @field:Size(min = 6, max = 36, message = "Password must be in range (6-32) characters")
    val confNewPassword: String
)

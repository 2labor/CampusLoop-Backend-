package com.example.project.campusloop.domain.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ResetPasswordRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Must be a valid email address")
    val email: String,

    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 6, max = 6, message = "Code must have 6 exactly characters")
    val code: String,

    @NotBlank(message = "New password must not be blank")
    @field:Size(min = 6, max = 32, message = "Password must be in range 6 to 32 characters")
    val newPassword: String,

    @field:NotBlank(message = "Confirm password must not be blank")
    @field:Size(min = 6, max = 32, message = "Password must be in range 6 to 32 characters")
    val confirmPassword: String
)

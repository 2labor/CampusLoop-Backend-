package com.example.project.campusloop.domain.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendVerificationRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Must be a valid email address")
    val email: String
)

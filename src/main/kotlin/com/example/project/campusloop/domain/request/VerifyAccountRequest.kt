package com.example.project.campusloop.domain.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class VerifyAccountRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Must be a valid email address")
    val email: String,

    @field:NotBlank(message = "Code must not be blank")
    @field:Size(min = 6, max = 6, message = "Code must be exactly 6 digits")
    val code: String
)

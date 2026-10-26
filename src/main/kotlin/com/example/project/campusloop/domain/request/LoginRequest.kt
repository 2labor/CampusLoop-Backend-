package com.example.project.campusloop.domain.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Credentials cannot be blank")
    val credentials: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)

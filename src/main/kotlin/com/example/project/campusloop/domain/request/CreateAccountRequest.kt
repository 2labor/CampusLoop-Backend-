package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.enum.AuthProvider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.NotNull


data class CreateAccountRequest(
    @field:NotNull(message = "User cannot be null")
    val provider: AuthProvider,

    @field:NotBlank(message = "Email must not be blank")
    val email: String,

    @field:NotBlank(message = "Login must not be blank")
    @field:Size(min = 3, max = 16, message = "Name must be in range (3-16) characters")
    val login: String,

    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 6, max = 32, message = "Password must be in range (6-32) digits")
    val password: String,

    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 6, max = 32, message = "Password must be in range (6-32) digits")
    val passwordConf: String
)
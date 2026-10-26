package com.example.project.campusloop.domain.request
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 4, max = 24, message = "User name must be in range 4 to 24 characters")
    val username: String
)
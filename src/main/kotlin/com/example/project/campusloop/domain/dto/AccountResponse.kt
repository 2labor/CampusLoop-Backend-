package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.AuthProvider
import java.util.UUID

data class AccountResponse(
    val id: UUID,
    val provider: AuthProvider,
    val email: String,
    val login: String,
)

package com.example.project.campusloop.security.domain.request

import com.example.project.campusloop.domain.enum.TokenType

data class ConsumeTokenRequest(
    val token: String,
    val type: TokenType
)

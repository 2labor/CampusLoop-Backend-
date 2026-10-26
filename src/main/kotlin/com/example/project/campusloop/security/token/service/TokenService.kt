package com.example.project.campusloop.security.token.service

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.Token

interface TokenService {
    fun createToken(identifier: String, tokenStr: String, tokenType: TokenType, durationMinutes: Long): Token
    fun validateAndConsume(identifier: String, tokenValue: String, type: TokenType): Boolean
}
package com.example.project.campusloop.security.token.repository

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.Token
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TokenRepository : JpaRepository<Token, UUID> {
    fun findByIdentifierAndType(identifier: String, type: TokenType): Token?
    fun deleteByIdentifierAndType(identifier: String, type: TokenType)
}
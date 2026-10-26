package com.example.project.campusloop.security.token

import com.example.project.campusloop.domain.enum.TokenType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tokens",
    indexes = [Index(name = "idx_identifier_type", columnList = "identifier,type")])
class Token (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "identifier", nullable = false)
    val identifier: String,

    @Column(name = "token", nullable = false)
    val tokenStr: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: TokenType,

    @Column(name = "expires_in", nullable = false)
    val expiresIn: LocalDateTime,
)
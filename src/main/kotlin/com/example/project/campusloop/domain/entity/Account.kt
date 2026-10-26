package com.example.project.campusloop.domain.entity

import com.example.project.campusloop.domain.enum.AuthProvider
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "accounts")
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    val provider: AuthProvider = AuthProvider.LOCAL,

    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "login", nullable = false)
    val login: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    val user: User,
)

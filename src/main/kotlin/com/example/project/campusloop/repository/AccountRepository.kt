package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {
    fun findByEmail(email: String): Account?

    fun findByLogin(username: String): Account?

    fun existsByEmail(email: String): Boolean

    fun existsByLogin(username: String): Boolean
}
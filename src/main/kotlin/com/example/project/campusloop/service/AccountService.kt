package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.enum.AuthProvider
import java.util.UUID

interface AccountService {
    fun createAccount(email: String, login: String, provider: AuthProvider, password: String, user: User): Account
    fun getAccountById(userId: UUID): Account?
    fun findByCredentials(credential: String): Account?
    fun markAsVerified(accountId: UUID)
}
package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.enum.AuthProvider
import com.example.project.campusloop.domain.request.CreateAccountRequest
import java.util.UUID

interface AccountService {
    fun createAccount(email: String, login: String, provider: AuthProvider, password: String, user: User): Account
    fun getAccountById(userId: UUID): Account?
}
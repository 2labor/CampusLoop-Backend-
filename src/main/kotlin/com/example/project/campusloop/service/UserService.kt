package com.example.project.campusloop.service.iml

import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.request.CreateAccountRequest
import java.util.UUID

interface UserService {
    fun createUser(request: CreateAccountRequest): User
    fun getUserById(userId: UUID): User?
}

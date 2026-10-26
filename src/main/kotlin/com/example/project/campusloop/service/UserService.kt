package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.request.CreateAccountRequest
import java.util.UUID

interface UserService {
    fun createUser(username: String): User
    fun getUserById(userId: UUID): User?
}

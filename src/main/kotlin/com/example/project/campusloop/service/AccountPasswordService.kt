package com.example.project.campusloop.service

import java.util.UUID

interface AccountPasswordService {
    fun forgotPassword(email: String)
    fun resetPassword(email: String, code: String, newPassword: String, confirmPassword: String)
    fun changePassword(userId: UUID, oldPassword: String, newPassword: String, confirmPassword: String)
}
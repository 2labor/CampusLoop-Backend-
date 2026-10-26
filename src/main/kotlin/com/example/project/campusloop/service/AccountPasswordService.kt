package com.example.project.campusloop.service

interface AccountPasswordService {
    fun forgotPassword(email: String)
    fun resetPassword(email: String, code: String, newPassword: String, confirmPassword: String)
}
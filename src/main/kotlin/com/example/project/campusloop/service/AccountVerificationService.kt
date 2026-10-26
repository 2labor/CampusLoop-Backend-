package com.example.project.campusloop.service

interface AccountVerificationService {
    fun sendVerificationCode(email: String)
    fun verify(email: String, code: String): Boolean
}
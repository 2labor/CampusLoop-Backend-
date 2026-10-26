package com.example.project.campusloop.service

interface EmailService {
    fun sendVerificationEmail(toEmail: String, code: String)
}
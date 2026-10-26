package com.example.project.campusloop.domain.command

data class LoginCommand (
    val credentials: String,
    val password: String
)
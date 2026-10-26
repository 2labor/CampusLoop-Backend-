package com.example.project.campusloop.domain.command

import com.example.project.campusloop.domain.enum.AuthProvider

data class RegisterCommand (
    val provider: AuthProvider,
    val email: String,
    val username: String,
    val login: String,
    val password: String,
    val passwordConf: String
)
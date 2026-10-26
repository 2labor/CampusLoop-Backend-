package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.LoginCommand
import com.example.project.campusloop.domain.command.RegisterCommand
import com.example.project.campusloop.domain.dto.AccountResponse
import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.domain.request.CreateAccountRequest
import com.example.project.campusloop.domain.request.LoginRequest

interface AuthMapper {
    fun toCommand(request: CreateAccountRequest): RegisterCommand
    fun toCommand(request: LoginRequest): LoginCommand
    fun toResponse(account: Account): AccountResponse
}
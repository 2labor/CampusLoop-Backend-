package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.RegisterCommand
import com.example.project.campusloop.domain.request.CreateAccountRequest

interface AuthMapper {
    fun toCommand(request: CreateAccountRequest): RegisterCommand
}
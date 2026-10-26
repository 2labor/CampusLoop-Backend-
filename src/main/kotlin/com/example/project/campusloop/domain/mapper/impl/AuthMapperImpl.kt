package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.RegisterCommand
import com.example.project.campusloop.domain.mapper.AuthMapper
import com.example.project.campusloop.domain.request.CreateAccountRequest
import org.springframework.stereotype.Component

@Component
class AuthMapperImpl : AuthMapper {

    override fun toCommand(request: CreateAccountRequest): RegisterCommand {
        return RegisterCommand(
            provider = request.provider,
            email = request.email,
            username = request.username,
            login = request.login,
            password = request.password,
            passwordConf = request.passwordConf
        )
    }
}
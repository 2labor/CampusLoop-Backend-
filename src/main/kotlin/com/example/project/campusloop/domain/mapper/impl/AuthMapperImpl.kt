package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.LoginCommand
import com.example.project.campusloop.domain.command.RegisterCommand
import com.example.project.campusloop.domain.dto.AccountResponse
import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.domain.mapper.AuthMapper
import com.example.project.campusloop.domain.request.CreateAccountRequest
import com.example.project.campusloop.domain.request.LoginRequest
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

    override fun toCommand(request: LoginRequest): LoginCommand {
        return LoginCommand(
            credentials = request.credentials,
            password = request.password,
        )
    }

    override fun toResponse(account: Account): AccountResponse {
        return AccountResponse(
            id = account.id!!,
            provider = account.provider,
            email = account.email,
            login = account.login,
        )
    }
}
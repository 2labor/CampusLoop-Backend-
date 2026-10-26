package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.command.RegisterCommand
import com.example.project.campusloop.domain.dto.AccountResponse
import com.example.project.campusloop.domain.dto.TokenResponse
import com.example.project.campusloop.domain.mapper.AuthMapper
import com.example.project.campusloop.domain.request.CreateAccountRequest
import com.example.project.campusloop.domain.request.LoginRequest
import com.example.project.campusloop.service.impl.AuthFacade
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authFacade: AuthFacade,
    private val mapper: AuthMapper
) {

    @PostMapping("/register")
    fun register(@RequestBody request: CreateAccountRequest): ResponseEntity<AccountResponse> {
        val account = authFacade.register(mapper.toCommand(request))
        return ResponseEntity.ok(mapper.toResponse(account))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val token = authFacade.login(mapper.toCommand(request))
        return ResponseEntity.ok(TokenResponse(token))
    }
}
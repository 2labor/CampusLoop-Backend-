package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.RegisterCommand
import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.service.AccountService
import com.example.project.campusloop.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthFacade (
    private val accountService: AccountService,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun register(command: RegisterCommand): Account {
        if (command.password != command.passwordConf) {
            throw IllegalArgumentException("Passwords do not match")
        }

        val newUser = userService.createUser(command.username)
        val hashPassword = passwordEncoder.encode(command.password)

        return accountService.createAccount(
            command.email,
            command.login,
            command.provider,
            hashPassword,
            newUser
        )
    }

}
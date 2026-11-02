package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.LoginCommand
import com.example.project.campusloop.domain.command.RegisterCommand
import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.security.JwtCore
import com.example.project.campusloop.service.AccountService
import com.example.project.campusloop.service.AccountVerificationService
import com.example.project.campusloop.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthFacade (
    private val accountService: AccountService,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtCore: JwtCore,
    private val verificationService: AccountVerificationService,

    @Value("\${campusloop.alowed-mail-domain}")
    private val universityDomain: String,

    @Value("\${campusloop.auth.require-verification:true}")
    private val requireVerification: Boolean = true
) {

    @Transactional
    fun register(command: RegisterCommand): Account {
        if (command.password != command.passwordConf) {
            throw IllegalArgumentException("Passwords do not match")
        }

        val normalizedEmail = command.email.trim().lowercase()
        val normalizedLogin = command.login.trim().lowercase()

        if (!isAllowedUniversityEmail(normalizedEmail, universityDomain)) {
            throw IllegalArgumentException("Registration is restricted to university email addresses ($universityDomain)")
        }

        val newUser = userService.createUser(command.username.trim())
        val hashPassword = passwordEncoder.encode(command.password)

        val account = accountService.createAccount(
            normalizedEmail,
            normalizedLogin,
            command.provider,
            hashPassword,
            newUser
        )

        if (requireVerification) {
            verificationService.sendVerificationCode(normalizedEmail)
        }

        return account
    }

    fun login(command: LoginCommand): String {
        val normalizedCredentials = command.credentials.trim()
        val account = accountService.findByCredentials(normalizedCredentials)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordEncoder.matches(command.password, account.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        if (requireVerification && !account.isVerified) {
            throw IllegalStateException("Account is not verified. Please check your email for the verification code.")
        }

        return jwtCore.generateToken(account.user.id!!)
    }

    companion object {
        fun isAllowedUniversityEmail(email: String, configuredDomain: String): Boolean {
            val normalizedEmail = email.trim().lowercase()
            val atIndex = normalizedEmail.lastIndexOf('@')
            if (atIndex <= 0 || atIndex == normalizedEmail.length - 1) {
                return false
            }
            val emailDomain = normalizedEmail.substring(atIndex + 1)
            val allowedDomain = configuredDomain.trim().lowercase().removePrefix("@").removePrefix(".")
            if (allowedDomain.isBlank()) {
                return false
            }
            return emailDomain == allowedDomain || emailDomain.endsWith(".$allowedDomain")
        }
    }
}

package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.service.TokenService
import com.example.project.campusloop.service.AccountPasswordService
import com.example.project.campusloop.service.AccountService
import com.example.project.campusloop.service.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class AccountPasswordServiceImpl(
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder,

    @Value("\${app.campus.password-reset-ttl-minutes:30}")
    private val passwordResetTtlMinutes: Long,
) : AccountPasswordService {

    @Transactional
    override fun forgotPassword(email: String) {
        val account = accountService.findByCredentials(email)
            ?: throw IllegalArgumentException("Account with email $email not found")

        val code = Random.nextInt(100000, 999999).toString()

        tokenService.createToken(
            identifier = email,
            tokenStr = code,
            TokenType.PASSWORD_RESET,
            durationMinutes = passwordResetTtlMinutes
        )

        emailService.sendPasswordResetEmail(email, code)
    }

    @Transactional
    override fun resetPassword(
        email: String,
        code: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (newPassword != confirmPassword) {
            throw IllegalArgumentException("Passwords do not match")
        }

        val isValid = tokenService.validateAndConsume(
            identifier = email,
            tokenValue = code,
            type = TokenType.PASSWORD_RESET
        )

        if (!isValid) {
            throw IllegalArgumentException("Invalid or expired password reset code")
        }

        val account = accountService.findByCredentials(email) ?:
        throw IllegalArgumentException("Account with email $email not found")

        val hashPassword = passwordEncoder.encode(newPassword)
        accountService.updatePassword(account.id!!, hashPassword)
    }
}
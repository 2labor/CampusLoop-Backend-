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
import java.security.SecureRandom
import java.util.UUID

@Service
class AccountPasswordServiceImpl (
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder,

    @Value("\${app.campus.password-reset-ttl-minutes:30}")
    private val passwordResetTtlMinutes: Long,
) : AccountPasswordService {

    private val secureRandom = SecureRandom()

    @Transactional
    override fun forgotPassword(email: String) {
        val normalizedEmail = email.trim().lowercase()
        val account = accountService.findByCredentials(normalizedEmail)
            ?: return // Return silently to prevent account enumeration (SEC-05)

        val code = (100000 + secureRandom.nextInt(900000)).toString()

        tokenService.createToken(
            identifier = account.email,
            tokenStr = code,
            TokenType.PASSWORD_RESET,
            durationMinutes = passwordResetTtlMinutes
        )

        emailService.sendPasswordResetEmail(account.email, code)
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

        val normalizedEmail = email.trim().lowercase()
        val token = tokenService.validateAndConsume(
            identifier = normalizedEmail,
            tokenValue = code,
            type = TokenType.PASSWORD_RESET
        )

        if (token == null) {
            throw IllegalArgumentException("Invalid or expired password reset code")
        }

        val account = accountService.findByCredentials(normalizedEmail)
            ?: throw IllegalArgumentException("Invalid or expired password reset code")

        val hashPassword = passwordEncoder.encode(newPassword)
        accountService.updatePassword(account.id!!, hashPassword)
    }

    @Transactional
    override fun changePassword(
        userId: UUID,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (newPassword != confirmPassword) {
            throw IllegalArgumentException("New passwords do not match")
        }

        val account = accountService.findByUserId(userId)
            ?: throw IllegalArgumentException("Account not found")

        if (!passwordEncoder.matches(oldPassword, account.passwordHash)) {
            throw IllegalArgumentException("Incorrect old password")
        }

        val hashPassword = passwordEncoder.encode(newPassword)
        accountService.updatePassword(account.id!!, hashPassword)
    }
}

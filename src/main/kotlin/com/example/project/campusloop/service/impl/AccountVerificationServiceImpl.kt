package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.service.TokenService
import com.example.project.campusloop.service.AccountService
import com.example.project.campusloop.service.AccountVerificationService
import com.example.project.campusloop.service.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class AccountVerificationServiceImpl(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val emailService: EmailService,

    @Value("\${campusloop.account.verification-ttl-minutes}")
    private val ttlForToken: Long
    ) : AccountVerificationService {

    override fun sendVerificationCode(email: String) {
        val account = accountService.findByCredentials(email) ?:
            throw IllegalArgumentException("No account found")

        if (account.isVerified) {
            throw IllegalArgumentException("Account already verified")
        }

        val code = Random.nextInt(100000, 999999).toString()

        tokenService.createToken(
            identifier = email,
            tokenStr = code,
            tokenType = TokenType.EMAIL_VERIFICATION,
            durationMinutes = ttlForToken,
        )

        emailService.sendVerificationEmail(email,code)
    }

    override fun verify(email: String, code: String): Boolean {
        val isVal = tokenService.validateAndConsume(email, code, TokenType.EMAIL_VERIFICATION)

        if (!isVal) {
            return false
        }

        val account = accountService.findByCredentials(email)
            ?: throw IllegalArgumentException("No account found")

        accountService.markAsVerified(account.id!!)
        return true
    }
}
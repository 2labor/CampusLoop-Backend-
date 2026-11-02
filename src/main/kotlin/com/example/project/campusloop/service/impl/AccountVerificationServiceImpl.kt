package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.service.TokenService
import com.example.project.campusloop.service.AccountService
import com.example.project.campusloop.service.AccountVerificationService
import com.example.project.campusloop.service.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class AccountVerificationServiceImpl(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val emailService: EmailService,

    @Value("\${campusloop.account.verification-ttl-minutes:15}")
    private val ttlForToken: Long
) : AccountVerificationService {

    private val secureRandom = SecureRandom()

    override fun sendVerificationCode(email: String) {
        val normalizedEmail = email.trim().lowercase()
        val account = accountService.findByCredentials(normalizedEmail)
            ?: return // Return silently to prevent account enumeration (SEC-05)

        if (account.isVerified) {
            return
        }

        val code = (100000 + secureRandom.nextInt(900000)).toString()

        tokenService.createToken(
            identifier = account.email,
            tokenStr = code,
            tokenType = TokenType.EMAIL_VERIFICATION,
            durationMinutes = ttlForToken,
        )

        emailService.sendVerificationEmail(account.email, code)
    }

    override fun verify(email: String, code: String): Boolean {
        val normalizedEmail = email.trim().lowercase()
        val token = tokenService.validateAndConsume(normalizedEmail, code, TokenType.EMAIL_VERIFICATION)
            ?: return false

        val account = accountService.findByCredentials(normalizedEmail)
            ?: return false

        accountService.markAsVerified(account.id!!)
        return true
    }
}

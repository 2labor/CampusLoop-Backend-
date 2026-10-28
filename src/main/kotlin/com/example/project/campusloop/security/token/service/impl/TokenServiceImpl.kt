package com.example.project.campusloop.security.token.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.Token
import com.example.project.campusloop.security.token.repository.TokenRepository
import com.example.project.campusloop.security.token.service.TokenService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class TokenServiceImpl(
    private val repository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
) : TokenService {

    @Transactional
    override fun createToken(
        identifier: String,
        tokenStr: String,
        tokenType: TokenType,
        durationMinutes: Long
    ): Token {
        repository.deleteByIdentifierAndType(identifier, tokenType)

        val hashToken = passwordEncoder.encode(tokenStr)

        val token = Token(
            identifier = identifier,
            tokenStr = hashToken,
            type = tokenType,
            expiresIn = Instant.now().plusSeconds(durationMinutes * 60)
        )

        return repository.save(token)
    }

    @Transactional
    override fun validateAndConsume(
        identifier: String,
        tokenValue: String,
        type: TokenType
    ): Boolean {
        val tokenEntity = repository.findByIdentifierAndType(identifier, type) ?: return false

        if (tokenEntity.expiresIn.isBefore(Instant.now())) {
            repository.delete(tokenEntity)
            return false
        }

        if (!passwordEncoder.matches(tokenValue, tokenEntity.tokenStr)) {
            return false
        }

        repository.delete(tokenEntity)
        return true
    }
}
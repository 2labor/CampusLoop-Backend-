package com.example.project.campusloop.security.token.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.token.Token
import com.example.project.campusloop.security.token.service.TokenService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class RedisTokenServiceImpl (
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val passwordEncoder: PasswordEncoder
) : TokenService {

    companion object {
        private const val TOKEN_PREFIX = "token"

        fun buildKey(identifier: String, type: TokenType): String {
            return "$TOKEN_PREFIX:$type:$identifier"
        }
    }

    override fun createToken(
        identifier: String,
        tokenStr: String,
        tokenType: TokenType,
        durationMinutes: Long,
        targetEntityId: UUID?,
        maxUses: Int?
    ): Token {

        val key = buildKey(identifier, tokenType)
        val hashToken = passwordEncoder.encode(tokenStr)

        val token = Token(
            identifier = identifier,
            tokenStr = hashToken,
            type = tokenType,
            expiresIn = Instant.now().plusSeconds(durationMinutes * 60),
            targetEntityId = targetEntityId,
            maxUses = maxUses,
            currentUses = 0
        )

        val jsonValue = objectMapper.writeValueAsString(token)

        redisTemplate.opsForValue().set(
            key,
            jsonValue,
            durationMinutes,
            TimeUnit.MINUTES,
        )

        return token
    }

    override fun validateAndConsume(
        identifier: String,
        tokenValue: String,
        type: TokenType
    ): Token? {

        val key = buildKey(identifier, type)
        val jsonValue = redisTemplate.opsForValue().get(key) ?: return null

        val token = try {
            objectMapper.readValue(jsonValue, Token::class.java)
        } catch (e: Exception) {
            return null
        }

        if (!passwordEncoder.matches(tokenValue, token.tokenStr)) {
            return null
        }

        if (token.maxUses != null) {
            val newUses = token.currentUses + 1
            if (newUses > token.maxUses) {
                redisTemplate.delete(key)
                return null
            }

            val updatedToken = token.copy(
                currentUses = newUses,
                updatedAt = Instant.now()
            )

            val expireSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS)
            if (expireSeconds != null && expireSeconds > 0) {
                redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(updatedToken),
                    expireSeconds,
                    TimeUnit.SECONDS
                )
            }

            return updatedToken
        }

        redisTemplate.delete(key)
        return token
    }
}
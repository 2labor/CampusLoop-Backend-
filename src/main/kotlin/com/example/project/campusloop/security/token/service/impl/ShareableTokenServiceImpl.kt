package com.example.project.campusloop.security.token.service.impl

import com.example.project.campusloop.domain.enum.TokenType
import com.example.project.campusloop.security.domain.entity.ShareableToken
import com.example.project.campusloop.security.domain.entity.StoredShareableToken
import com.example.project.campusloop.security.token.service.ShareableTokenService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class ShareableTokenServiceImpl(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : ShareableTokenService {

    companion object {
        private const val PREFIX = "share_token"

        private fun buildKey(rawToken: String, type: TokenType): String {
            return "$PREFIX$rawToken:$type"
        }
    }

    override fun createToken(
        type: TokenType,
        targetEntityId: String,
        durationInMinutes: Long,
        maxUses: Int?
    ): ShareableToken {
        val rawToken = UUID.randomUUID().toString()
        val key = buildKey(sha256(rawToken), type)

        val expiresAt = Instant.now().plusSeconds(durationInMinutes * 60)
        val stored = StoredShareableToken(
            type = type,
            targetEntityId = targetEntityId,
            expiredIn = expiresAt,
            maxUses = maxUses
        )

        redisTemplate.opsForValue().set(
            key,
            objectMapper.writeValueAsString(stored),
            durationInMinutes,
            TimeUnit.MINUTES
        )

        return ShareableToken(
            rawToken = rawToken,
            type = type,
            targetEntityId = targetEntityId,
            expiresIn = expiresAt,
            maxUses = maxUses
        )
    }

    override fun consumeToken(
        tokenValue: String,
        expectedType: TokenType
    ): StoredShareableToken? {
        val key = buildKey(sha256(tokenValue), expectedType)
        val jsonValue = redisTemplate.opsForValue().get(key) ?: return null

        val stored = try {
            objectMapper.readValue(jsonValue, StoredShareableToken::class.java)
        } catch (e: Exception) {
            return null
        }

        if (stored.maxUses == null) {
            return stored
        }

        val newUses = stored.currentUses?.plus(1)
        if (newUses != null) {
            if (newUses > stored.maxUses) {
                redisTemplate.delete(key)
                return null
            }
        }

        val updated = stored.copy(currentUses = newUses)

        if (newUses == stored.maxUses) {
            redisTemplate.delete(key)
        } else {
            val expireSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS)
            if (expireSeconds > 0) {
                redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(updated),
                    expireSeconds,
                    TimeUnit.SECONDS
                )
            }
        }
        return updated
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
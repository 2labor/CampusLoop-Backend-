package com.example.project.campusloop.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtCore (
    @Value("\${campusloop.jwt.secret}")
    private val secret: String,

    @Value("\${campusloop.jwt.expiration:86400000}")
    private val ttl: Long,

    @Value("\${campusloop.jwt.issuer:campusloop-api}")
    private val issuer: String
) {
    init {
        require(secret.toByteArray(StandardCharsets.UTF_8).size >= 32) {
            "JWT secret must be at least 32 bytes (256 bits) for HMAC-SHA256"
        }
    }

    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(userId: UUID): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .setIssuer(issuer)
            .setSubject(userId.toString())
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + ttl))
            .signWith(key)
            .compact()
    }

    fun validToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: JwtException) {
            return false
        } catch (ex: IllegalArgumentException) {
            return false
        }
    }

    fun getUserIdFromToken(token: String): UUID {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .requireIssuer(issuer)
            .build()
            .parseClaimsJws(token)
            .body
        return UUID.fromString(claims.subject)
    }

}

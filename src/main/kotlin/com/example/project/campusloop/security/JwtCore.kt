package com.example.project.campusloop.security

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import io.jsonwebtoken.security.Keys
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtCore (
    @Value("\${campusloop.jwt.secret}")
    private val secret: String,

    @Value("\${campusloop.jwt.expiration:86400000}")
    private val ttl: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(userId: UUID): String {
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + ttl))
            .signWith(key)
            .compact()
    }
}
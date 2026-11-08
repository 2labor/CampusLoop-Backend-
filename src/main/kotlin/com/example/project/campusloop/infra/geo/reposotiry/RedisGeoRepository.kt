package com.example.project.campusloop.infra.geo.reposotiry

import com.example.project.campusloop.infra.geo.domain.dto.UserLocationDto
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.domain.geo.GeoReference
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.util.UUID
@Repository
class RedisGeoRepository (
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private const val GEO_KEY = "user:locations"
        private const val USER_TTL_KEY_PREFIX = "user:location:ttl:"
        private val EXPIRATION_TIMEOUT = Duration.ofSeconds(30)
    }

    fun updateUserLocation(userId: UUID, latitude: Double, longitude: Double){
        val memberName = userId.toString()

        redisTemplate.opsForGeo().add(
            GEO_KEY,
            Point(latitude, longitude),
            memberName
        )

        val ttlKey = "$USER_TTL_KEY_PREFIX$memberName"
        redisTemplate.opsForValue().set(ttlKey, Instant.now().toEpochMilli().toString(), EXPIRATION_TIMEOUT)
    }

    fun removeUserLocation(userId: UUID) {
        val memberName = userId.toString()
        redisTemplate.opsForZSet().remove(GEO_KEY, memberName)
        redisTemplate.delete("$USER_TTL_KEY_PREFIX$memberName")
    }

    fun findUsersInRadius(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Double,
        limit: Long = 100,
    ): List<UserLocationDto> {
        val center = Point(longitude, latitude)

        val reference = GeoReference.fromCoordinate<String>(center)
        val distance = Distance(radiusInMeters, Metrics.NEUTRAL)

        val args = RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
            .includeCoordinates()
            .limit(limit)

        val results = redisTemplate.opsForGeo().search(
            GEO_KEY,
            reference,
            distance,
            args
        ) ?: return emptyList()

        return results.content.mapNotNull { result ->
            val userIdStr = result.content.name
            val point = result.content.point

            if (point != null) {
                UserLocationDto(
                    userId = UUID.fromString(userIdStr),
                    latitude = point.y,
                    longitude = point.x
                )
            } else null
        }
    }

    fun getUserLocation(userId: UUID): UserLocationDto? {
        val position = redisTemplate.opsForGeo().position(GEO_KEY, userId.toString()) ?: return null
        val point = position.firstOrNull() ?: return null

        return UserLocationDto(
            userId = userId,
            latitude = point.y,
            longitude = point.x
        )
    }
}
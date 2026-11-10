package com.example.project.campusloop.infra.geo.reposotiry

import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.domain.geo.GeoReference
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RedisGeoStoriesRepository (
    private val redisTemplate: StringRedisTemplate
) {
    private val key = "campus:stories:geo"

    fun addStoryLocation(storyId: UUID, latitude: Double, longitude: Double) {
        redisTemplate.opsForGeo().add(
            key,
            Point(latitude, longitude),
            storyId.toString(),
        )
    }

    fun findStoriesInRadius(latitude: Double, longitude: Double, radiusInMeters: Double): List<UUID> {
        val reference = GeoReference.fromCoordinate<String>(longitude, latitude)
        val distance = Distance(radiusInMeters, Metrics.NEUTRAL)
        val args = RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().limit(50)

        val results = redisTemplate.opsForGeo().search(key, reference,distance, args) ?: return emptyList()

        return results.mapNotNull { result ->
            runCatching { UUID.fromString(result.content.name) }.getOrNull()
        }
    }

    fun removeStoryLocation(storyId: UUID) {
        redisTemplate.opsForZSet().remove(key, storyId.toString())
    }

}
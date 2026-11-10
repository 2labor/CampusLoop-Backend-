package com.example.project.campusloop.infra.scheduler.service

import com.example.project.campusloop.infra.geo.reposotiry.RedisGeoStoriesRepository
import com.example.project.campusloop.repository.StoriesRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.math.log

@Component
class StoryCleanupScheduler (
    private val repository: StoriesRepository,
    private val redisRepository: RedisGeoStoriesRepository
) {

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    fun cleanupExpiredStories() {
        val now = Instant.now()

        val expiredStories = repository.findAllByExpiresAtBefore(now)
        if (expiredStories.isNotEmpty()) return

        expiredStories.forEach { story ->
            story.id?.let { redisRepository.removeStoryLocation(it) }
        }
    }

}
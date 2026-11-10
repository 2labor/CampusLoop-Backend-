package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Story
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface StoriesRepository : JpaRepository<Story, UUID> {
    @Query("SELECT s FROM Story s WHERE s.author.id = :authorId AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    fun findActiveStoriesByAuthorId(
        authorId: UUID,
        now: Instant,
        pageable: Pageable
    ): Slice<Story>

    // Список активных сторис по списку ID из Redis Geo
    @Query("SELECT s FROM Story s WHERE s.id IN :ids AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    fun findActiveStoriesByIds(
        ids: List<UUID>,
        now: Instant
    ): List<Story>

    fun findAllByExpiresAtBefore(now: Instant): List<Story>

    @Modifying
    @Query("DELETE FROM Story s WHERE s.expiresAt < :now")
    fun deleteAllExpiredStories(now: Instant): Int
}
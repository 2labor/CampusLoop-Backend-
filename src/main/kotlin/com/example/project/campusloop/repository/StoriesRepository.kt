package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Story
import com.example.project.campusloop.domain.enum.StoriesPrivacy
import io.lettuce.core.dynamic.annotation.Param
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

    @Query(
        """
        SELECT s FROM Story s
        JOIN FETCH s.author
        WHERE s.expiresAt > :now
          AND s.author.id IN :friendIds
          AND s.privacySettings IN :allowedPrivacies
        ORDER BY s.createdAt ASC
        """
    )
    fun findActiveFriendsStories(
        @Param("friendIds") friendIds: Collection<UUID>,
        @Param("allowedPrivacies") allowedPrivacies: Collection<StoriesPrivacy> = listOf(
            StoriesPrivacy.EVERYONE,
            StoriesPrivacy.FRIENDS
        ),
        @Param("now") now: Instant = Instant.now()
    ): List<Story>
}
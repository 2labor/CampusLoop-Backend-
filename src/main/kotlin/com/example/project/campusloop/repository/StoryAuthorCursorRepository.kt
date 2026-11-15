package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.StoryAuthorCursor
import com.example.project.campusloop.domain.entity.StoryAuthorCursorId
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface StoryAuthorCursorRepository : JpaRepository<StoryAuthorCursor, StoryAuthorCursorId> {
    fun findAllByIdViewerIdAndIdAuthorIdIn(
        viewerId: UUID,
        authorIds: Collection<UUID>
    ): List<StoryAuthorCursor>

    @Modifying
    @Query(
        value = """
        INSERT INTO story_author_cursors (viewer_id, author_id, last_viewed_at, updated_at)
        VALUES (:viewerId, :authorId, :viewedAt, CURRENT_TIMESTAMP)
        ON CONFLICT (viewer_id, author_id)
        DO UPDATE SET 
            last_viewed_at = GREATEST(story_author_cursors.last_viewed_at, EXCLUDED.last_viewed_at),
            updated_at = CURRENT_TIMESTAMP
        """,
        nativeQuery = true
    )
    fun upsertCursor(
        @Param("viewerId") viewerId: UUID,
        @Param("authorId") authorId: UUID,
        @Param("viewedAt") viewedAt: Instant
    )
}
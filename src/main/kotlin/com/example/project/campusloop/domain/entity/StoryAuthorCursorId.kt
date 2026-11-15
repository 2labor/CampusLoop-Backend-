package com.example.project.campusloop.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
class StoryAuthorCursorId (
    @Column(name = "viewer_id", nullable = false)
    val viewerId: UUID,

    @Column(name = "author_id", nullable = false)
    val authorId: UUID,
): Serializable
package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.NotificationType
import java.time.Instant
import java.util.UUID

data class SystemAnnouncementDto(
    val id: UUID = UUID.randomUUID(), // leave it for now as a random, maybe i will find some better solution
    val title: String,
    val body: String,
    val type: NotificationType = NotificationType.SYSTEM_ANNOUNCEMENT,
    val createdAt: Instant = Instant.now(),
)
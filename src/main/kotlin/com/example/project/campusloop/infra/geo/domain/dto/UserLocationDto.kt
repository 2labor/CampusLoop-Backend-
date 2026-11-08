package com.example.project.campusloop.infra.geo.domain.dto

import java.time.Instant
import java.util.UUID

data class UserLocationDto(
    val userId: UUID,
    val longitude: Double,
    val latitude: Double,
    val updatedAt: Instant = Instant.now(),
)

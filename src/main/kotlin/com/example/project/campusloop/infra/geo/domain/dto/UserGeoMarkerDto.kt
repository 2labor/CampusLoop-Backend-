package com.example.project.campusloop.infra.geo.domain.dto

import java.time.Instant
import java.util.UUID

data class UserGeoMarkerDto(
    val userId: UUID,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val isIdle: Boolean = false,
    val updatedAt: Instant = Instant.now()
)

package com.example.project.campusloop.infra.geo.domain.dto

data class GeoViewportDto(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double,
    val zoonLevel: Float
)

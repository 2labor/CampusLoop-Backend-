package com.example.project.campusloop.domain.request

data class GetStoriesInRadiusRequest(
    val latitude: Double,
    val longitude: Double,
    val radiusInMeters: Double
)

package com.example.project.campusloop.domain.command

data class GetStoriesInRadiusCommand(
    val latitude: Double,
    val longitude: Double,
    val radiusInMeters: Double
)

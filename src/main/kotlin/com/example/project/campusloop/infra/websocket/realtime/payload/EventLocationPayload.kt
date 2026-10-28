package com.example.project.campusloop.infra.websocket.realtime.payload

data class EventLocationPayload(
    val locationName: String,
    val latitude: Double,
    val longitude: Double
)

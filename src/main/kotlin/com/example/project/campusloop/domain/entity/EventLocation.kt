package com.example.project.campusloop.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class EventLocation(
    @Column(name = "location_name")
    val locationName: String,

    @Column(name = "latitude", nullable = false)
    val latitude: Double,

    @Column(name = "longitude", nullable = false)
    val longitude: Double
)
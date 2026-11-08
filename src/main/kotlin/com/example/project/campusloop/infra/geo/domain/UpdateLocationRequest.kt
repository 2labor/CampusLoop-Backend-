package com.example.project.campusloop.infra.geo.domain

import com.example.project.campusloop.infra.geo.domain.command.UpdateLocationCommand

data class UpdateLocationRequest(
    val longitude: Double,
    val latitude: Double
)

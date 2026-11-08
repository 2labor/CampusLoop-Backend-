package com.example.project.campusloop.infra.geo.domain.command

import com.example.project.campusloop.infra.geo.domain.UpdateLocationRequest

data class UpdateLocationCommand(
    val longitude: Double,
    val latitude: Double
)

fun UpdateLocationRequest.toCommand() = UpdateLocationCommand(
    longitude,
    latitude
)

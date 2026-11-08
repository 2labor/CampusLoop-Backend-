package com.example.project.campusloop.infra.geo.service

import com.example.project.campusloop.infra.geo.domain.command.UpdateLocationCommand
import com.example.project.campusloop.infra.geo.domain.dto.UserLocationDto
import java.util.UUID

interface GeoLocationService {
    fun updateLocation(userId: UUID, command: UpdateLocationCommand)
    fun enableGhostMode(userId: UUID)
    fun getFriendsLocations(userId: UUID, radiusInMeters: Double): List<UserLocationDto>
}
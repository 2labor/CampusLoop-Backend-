package com.example.project.campusloop.controller

import com.example.project.campusloop.infra.geo.domain.UpdateLocationRequest
import com.example.project.campusloop.infra.geo.domain.command.UpdateLocationCommand
import com.example.project.campusloop.infra.geo.domain.command.toCommand
import com.example.project.campusloop.infra.geo.domain.dto.UserLocationDto
import com.example.project.campusloop.infra.geo.service.GeoLocationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/geo")
class GeoLocationController (
    private val service: GeoLocationService
) {

    @PostMapping("/location")
    fun updateLocations(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: UpdateLocationRequest
    ): ResponseEntity<Void> {
        service.updateLocation(userId, request.toCommand())
        return ResponseEntity.ok().build()
    }


    @DeleteMapping("/ghost")
    fun enableGhostMode(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<Void> {
        service.enableGhostMode(userId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/friends")
    fun getFriendsLocation(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(defaultValue = "5000.0") radiusMeters: Double
    ): ResponseEntity<List<UserLocationDto>> {
        val locations = service.getFriendsLocations(userId, radiusMeters)
        return ResponseEntity.ok(locations)
    }
}
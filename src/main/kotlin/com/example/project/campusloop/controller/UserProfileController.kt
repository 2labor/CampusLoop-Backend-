package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.UserProfileResponse
import com.example.project.campusloop.domain.entity.UserProfile
import com.example.project.campusloop.domain.mapper.UserProfileMapper
import com.example.project.campusloop.domain.request.UpdateProfileRequest
import com.example.project.campusloop.service.UserProfileService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/profiles")
class UserProfileController (
    private val service: UserProfileService,
    private val mapper: UserProfileMapper
) {

    @GetMapping("/me")
    fun getMyProfile(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<UserProfileResponse> {
        val profile = service.getProfileByUserId(userId)
        return ResponseEntity.ok(mapper.toResponse(profile))
    }

    @GetMapping("/{userId}")
    fun getProfile(
        @PathVariable userId: UUID
    ): ResponseEntity<UserProfileResponse> {
        val profile = service.getProfileByUserId(userId)
        return ResponseEntity.ok(mapper.toResponse(profile))
    }

    @PutMapping("/me")
    fun updateProfile(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<UserProfileResponse> {
        val profile = service.updateProfile(userId, mapper.toCommand(request))
        return ResponseEntity.ok(mapper.toResponse(profile))
    }

}
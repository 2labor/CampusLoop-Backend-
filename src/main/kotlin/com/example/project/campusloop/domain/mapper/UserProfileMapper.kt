package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.UpdateProfileCommand
import com.example.project.campusloop.domain.dto.UserProfileResponse
import com.example.project.campusloop.domain.entity.UserProfile
import com.example.project.campusloop.domain.request.UpdateProfileRequest

interface UserProfileMapper {
    fun toCommand(request: UpdateProfileRequest): UpdateProfileCommand
    fun toResponse(entity: UserProfile): UserProfileResponse
}
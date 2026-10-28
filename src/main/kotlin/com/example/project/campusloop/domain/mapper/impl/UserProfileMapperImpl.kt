package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.UpdateProfileCommand
import com.example.project.campusloop.domain.dto.UserProfileResponse
import com.example.project.campusloop.domain.entity.UserProfile
import com.example.project.campusloop.domain.mapper.UserProfileMapper
import com.example.project.campusloop.domain.request.UpdateEventRequest
import com.example.project.campusloop.domain.request.UpdateProfileRequest
import org.springframework.stereotype.Component

@Component
class UserProfileMapperImpl : UserProfileMapper {

    override fun toCommand(request: UpdateProfileRequest): UpdateProfileCommand {
        return UpdateProfileCommand(
            fullName = request.fullName,
            avatarUrl = request.avatarUrl,
            bio = request.bio,
            course = request.course,
            academicYear = request.academicYear,
            campus = request.campus,
            interests = request.interests
        )
    }

    override fun toResponse(entity: UserProfile): UserProfileResponse {
        return UserProfileResponse(
            userId = entity.user.id!!,
            username = entity.user.username,
            fullName = entity.fullName,
            avatarUrl = entity.avatarUrl,
            bio = entity.bio,
            course = entity.course,
            academicYear = entity.academicYear,
            campus = entity.campus,
            interests = entity.interests
        )
    }
}
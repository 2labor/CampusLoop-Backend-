package com.example.project.campusloop.service

import com.example.project.campusloop.domain.command.UpdateProfileCommand
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.entity.UserProfile
import java.util.UUID

interface UserProfileService {
    fun getProfileByUserId(userId: UUID): UserProfile
    fun createDefaultProfile(user: User): UserProfile
    fun updateProfile(userId: UUID, command: UpdateProfileCommand): UserProfile
}
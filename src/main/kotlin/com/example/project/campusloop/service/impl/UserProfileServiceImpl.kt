package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.UpdateProfileCommand
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.entity.UserProfile
import com.example.project.campusloop.repository.UserProfileRepository
import com.example.project.campusloop.service.UserProfileService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileServiceImpl (
    private val repository: UserProfileRepository
) : UserProfileService {

    @Transactional(readOnly = true)
    override fun getProfileByUserId(userId: UUID): UserProfile {
        return repository.findByIdOrNull(userId) ?:
            throw IllegalArgumentException("User profile not found by id: $userId")
    }

    @Transactional
    override fun createDefaultProfile(user: User): UserProfile {
        val profile = UserProfile(
            id = user.id,
            user = user,
            fullName = user.username
        )

        return repository.save(profile)
    }

    @Transactional
    override fun updateProfile(
        userId: UUID,
        command: UpdateProfileCommand
    ): UserProfile {
        val profile = getProfileByUserId(userId)

        command.fullName.let { profile.fullName = it }
        command.avatarUrl.let { profile.avatarUrl = it }
        command.bio.let { profile.bio = it }
        command.course?.let { profile.course = it }
        command.academicYear?.let { profile.academicYear = it }
        command.campus?.let { profile.campus = it }
        command.interests?.let { profile.interests = it.toMutableSet() }

        return repository.save(profile)
    }
}
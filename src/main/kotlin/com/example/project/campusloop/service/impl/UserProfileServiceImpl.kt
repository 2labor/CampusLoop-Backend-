package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.UpdateProfileCommand
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.entity.UserProfile
import com.example.project.campusloop.domain.enum.FriendshipStatus
import com.example.project.campusloop.repository.FriendshipRepository
import com.example.project.campusloop.repository.UserProfileRepository
import com.example.project.campusloop.repository.UserRepository
import com.example.project.campusloop.service.UserProfileService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileServiceImpl (
    private val repository: UserProfileRepository,
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository
) : UserProfileService {

    @Transactional(readOnly = true)
    override fun getProfileByUserId(userId: UUID): UserProfile {
        return repository.findByIdOrNull(userId)
            ?: throw IllegalArgumentException("User profile not found by id: $userId")
    }

    @Transactional(readOnly = true)
    override fun getProfileForViewer(callerId: UUID, targetUserId: UUID): UserProfile {
        if (callerId != targetUserId) {
            val caller = userRepository.findByIdOrNull(callerId)
            val target = userRepository.findByIdOrNull(targetUserId)
            if (caller != null && target != null) {
                val connection = friendshipRepository.findBetweenUsers(caller, target).orElse(null)
                if (connection != null && connection.status == FriendshipStatus.BLOCKED) {
                    throw AccessDeniedException("Profile is not accessible")
                }
            }
        }

        return getProfileByUserId(targetUserId)
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

        command.fullName.let { profile.fullName = it.trim() }
        command.avatarUrl.let { profile.avatarUrl = it?.trim() }
        command.bio.let { profile.bio = it?.trim() }
        command.course?.let { profile.course = it.trim() }
        command.academicYear?.let { profile.academicYear = it }
        command.campus?.let { profile.campus = it.trim() }
        command.interests?.let { profile.interests = it.toMutableSet() }

        return repository.save(profile)
    }
}

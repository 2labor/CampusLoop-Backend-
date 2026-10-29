package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.repository.UserRepository
import com.example.project.campusloop.service.UserProfileService
import com.example.project.campusloop.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserServiceImpl (
    private val repository: UserRepository,
    private val profileService: UserProfileService
) : UserService {

    @Transactional
    override fun createUser(username: String): User {
        val user = User(
            username = username
        )

        val saved = repository.save(user)

        profileService.createDefaultProfile(saved)

        return saved
    }

    @Transactional(readOnly = true)
    override fun getUserById(userId: UUID): User? {
        return repository.findByIdOrNull(userId)
    }

    override fun existById(userId: UUID): Boolean {
        return repository.existsById(userId)
    }
}
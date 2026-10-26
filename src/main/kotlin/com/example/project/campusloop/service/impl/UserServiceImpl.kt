package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.repository.UserRespository
import com.example.project.campusloop.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserServiceImpl(
    private val repository: UserRespository
) : UserService {

    @Transactional
    override fun createUser(username: String): User {
        val user = User(
            username = username
        )

        return repository.save(user)
    }

    @Transactional(readOnly = true)
    override fun getUserById(userId: UUID): User? {
        return repository.findByIdOrNull(userId)
    }
}
package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID>{

}
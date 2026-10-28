package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
}
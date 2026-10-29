package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.UserProfile
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
    @Query("""
        SELECT p FROM UserProfile p 
        JOIN p.user u 
        WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
        """)
    fun searchProfiles(@Param("query") query: String, pageable: Pageable): Slice<UserProfile>
}
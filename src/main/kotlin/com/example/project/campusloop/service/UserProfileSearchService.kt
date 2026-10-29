package com.example.project.campusloop.service

import com.example.project.campusloop.domain.dto.UserProfileResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface UserProfileSearchService {
    fun searchUsers(query: String, pageable: Pageable): Slice<UserProfileResponse>
}
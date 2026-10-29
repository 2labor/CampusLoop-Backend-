package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.dto.UserProfileResponse
import com.example.project.campusloop.domain.mapper.UserProfileMapper
import com.example.project.campusloop.repository.UserProfileRepository
import com.example.project.campusloop.service.UserProfileSearchService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileSearchServiceImpl (
    private val repository: UserProfileRepository,
    private val mapper: UserProfileMapper
): UserProfileSearchService {

    @Transactional(readOnly = true)
    override fun searchUsers(
        query: String,
        pageable: Pageable
    ): Slice<UserProfileResponse> {
        return repository.searchProfiles(query, pageable)
            .map { mapper.toResponse(it) }
    }
}
package com.example.project.campusloop.security.domain.mapper

import com.example.project.campusloop.security.domain.dto.ActionTokenResponse
import com.example.project.campusloop.security.domain.entity.ShareableToken

interface TokenMapper {
    fun toResponse(token: ShareableToken): ActionTokenResponse
}
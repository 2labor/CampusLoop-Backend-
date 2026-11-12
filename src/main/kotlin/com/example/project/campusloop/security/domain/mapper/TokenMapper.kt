package com.example.project.campusloop.security.domain.mapper

import com.example.project.campusloop.security.domain.dto.ActionTokenResponse
import com.example.project.campusloop.security.domain.entity.Token

interface TokenMapper {
    fun toResponse(token: Token): ActionTokenResponse
}
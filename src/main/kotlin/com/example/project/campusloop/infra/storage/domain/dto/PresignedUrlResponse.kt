package com.example.project.campusloop.infra.storage.domain.dto

data class PresignedUrlResponse(
    val uploadUrl: String,
    val fileKey: String
)

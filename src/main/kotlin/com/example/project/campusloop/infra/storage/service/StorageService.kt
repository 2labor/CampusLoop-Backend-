package com.example.project.campusloop.infra.storage.service

import com.example.project.campusloop.infra.storage.domain.dto.PresignedUrlResponse

interface StorageService {
    fun generatePresignedUploadUrl(folderName: String, fileName: String): PresignedUrlResponse
    fun getFileUrl(fileKey: String): String
    fun deleteFile(fileKey: String)
}
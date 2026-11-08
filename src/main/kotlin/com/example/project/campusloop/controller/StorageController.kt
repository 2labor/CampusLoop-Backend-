package com.example.project.campusloop.controller

import com.example.project.campusloop.infra.storage.domain.dto.PresignedUrlResponse
import com.example.project.campusloop.infra.storage.domain.request.CreateUploadUrlRequest
import com.example.project.campusloop.infra.storage.domain.request.DeleteFileRequest
import com.example.project.campusloop.infra.storage.service.StorageService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/storage")
class StorageController (
    private val service: StorageService
) {

    @PostMapping("/upload-url")
    fun getUploadUrl(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: CreateUploadUrlRequest
    ): ResponseEntity<PresignedUrlResponse> {
        val userScopedFolder = "${request.folderName}/$userId"
        val response = service.generatePresignedUploadUrl(userScopedFolder, request.fileName)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping
    fun deleteFile(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: DeleteFileRequest
    ): ResponseEntity<Void> {
        if (!request.fileKey.contains(userId.toString())) {
            return ResponseEntity.status(403).build()
        }

        service.deleteFile(request.fileKey)
        return ResponseEntity.noContent().build()
    }

}
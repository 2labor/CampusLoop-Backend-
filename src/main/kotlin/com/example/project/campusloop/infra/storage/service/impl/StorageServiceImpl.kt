package com.example.project.campusloop.infra.storage.service.impl

import com.example.project.campusloop.infra.storage.domain.dto.PresignedUrlResponse
import com.example.project.campusloop.infra.storage.service.StorageService
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import io.minio.http.Method
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class StorageServiceImpl (
    private val minioClient: MinioClient,
    @Value("\${app.s3.bucket-name}")
    private val bucketName: String
) : StorageService {
    override fun generatePresignedUploadUrl(
        folderName: String,
        fileName: String
    ): PresignedUrlResponse {
        val extension = fileName.substringAfterLast(".", "")
        val fileKey = "$folderName/${UUID.randomUUID()}${if (extension.isNotEmpty()) ".$extension" else ""}"

        val uploadUrl = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(bucketName)
                .`object`(fileKey)
                .expiry(15, TimeUnit.MINUTES)
                .build()
        )

        return PresignedUrlResponse(
            uploadUrl = uploadUrl,
            fileKey = fileKey
        )
    }

    override fun getFileUrl(fileKey: String): String {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .`object`(fileKey)
                .expiry(7, TimeUnit.DAYS)
                .build()
        )
    }

    override fun deleteFile(fileKey: String) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileKey)
                .build()
        )
    }
}
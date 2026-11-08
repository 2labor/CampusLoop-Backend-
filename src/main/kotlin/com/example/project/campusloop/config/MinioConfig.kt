package com.example.project.campusloop.config

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.SetBucketLifecycleArgs
import io.minio.messages.Expiration
import io.minio.messages.LifecycleConfiguration
import io.minio.messages.LifecycleRule
import io.minio.messages.RuleFilter
import io.minio.messages.Status
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig(
    @Value("\${app.s3.endpoint}") private val endpoint: String,
    @Value("\${app.s3.access-key}") private val accessKey: String,
    @Value("\${app.s3.secret-key}") private val secretKey: String,
    @Value("\${app.s3.region}") private val region: String,
    @Value("\${app.s3.bucket-name}") private val bucketName: String
) {
    @Bean
    fun minioClient(): MinioClient {
        val client = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .region(region)
            .build()

        val bucketExists = client.bucketExists(
            BucketExistsArgs.builder().bucket(bucketName).build()
        )
        if (!bucketExists) {
            client.makeBucket(
                MakeBucketArgs.builder().bucket(bucketName).build()
            )
        }

        val rule = LifecycleRule(
            Status.ENABLED,
            null,
            Expiration(null as io.minio.messages.ResponseDate?, 1, null), // Явный каст null
            RuleFilter("temp/"),
            "delete-temp-files-after-24h",
            null,
            null,
            null
        )

        client.setBucketLifecycle(
            SetBucketLifecycleArgs.builder()
                .bucket(bucketName)
                .config(LifecycleConfiguration(listOf(rule)))
                .build()
        )

        return client
    }
}
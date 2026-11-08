package com.example.project.campusloop.infra.storage.domain.request

data class CreateUploadUrlRequest(
    val folderName: String,
    val fileName: String,
)
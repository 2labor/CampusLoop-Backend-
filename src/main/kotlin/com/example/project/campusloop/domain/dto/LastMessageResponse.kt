package com.example.project.campusloop.domain.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant

data class LastMessageResponse(
    @field:JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val content: String,
    val senderName: String,
    val createdAt: Instant
)
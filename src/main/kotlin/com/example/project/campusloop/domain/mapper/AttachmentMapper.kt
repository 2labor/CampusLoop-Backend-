package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.dto.AttachmentResponse
import com.example.project.campusloop.domain.entity.Attachment

interface AttachmentMapper {
    fun toResponseList(attachments: List<Attachment>): List<AttachmentResponse>
}
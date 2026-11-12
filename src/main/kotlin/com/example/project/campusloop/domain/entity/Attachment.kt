package com.example.project.campusloop.domain.entity

import com.example.project.campusloop.domain.enum.FileType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "attachments")
class Attachment (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "file_size", nullable = false)
    val fileSize: Long,

    @Column(name = "original_name", nullable = false)
    val originalName: String,

    @Column(name = "file_key", nullable = false)
    val fileKey: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    val fileType: FileType,

    @Column(name = "mime_type", nullable = false)
    val mimeType: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    var message: Message? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    var post: Post? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.CommentResponse
import com.example.project.campusloop.domain.mapper.CommentMapper
import com.example.project.campusloop.domain.request.CreateCommentRequest
import com.example.project.campusloop.domain.request.EditCommentRequest
import com.example.project.campusloop.service.CommentService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/groups/{groupId}/posts/{postId}/comments")
class CommentController(
    private val service: CommentService,
    private val mapper: CommentMapper
) {
    @PostMapping
    fun createComment(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID,
        @RequestBody request: CreateCommentRequest
    ): ResponseEntity<CommentResponse> {
        val comment = service.createComment(userId, groupId, postId, mapper.toCommand(request))

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(comment))
    }

    @PutMapping("/{commentId}")
    fun editComment(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID,
        @PathVariable commentId: UUID,
        @RequestBody request: EditCommentRequest
    ): ResponseEntity<CommentResponse> {
        val comment = service.editComment(userId, commentId, mapper.toCommand(request))

        return ResponseEntity.ok(mapper.toResponse(comment))
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID,
        @PathVariable commentId: UUID
    ): ResponseEntity<Void> {
        service.deleteComment(userId, postId, commentId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{commentId}")
    fun getComment(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID,
        @PathVariable commentId: UUID
    ): ResponseEntity<CommentResponse> {
        val comment = service.getComment(userId, commentId)

        return ResponseEntity.ok(mapper.toResponse(comment))
    }

    @GetMapping
    fun getCommentsByPost(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<CommentResponse>> {
        val slice = service.getCommentsByPost(userId, postId, pageable)
        val content = slice.content.map { mapper.toResponse(it) }
        val responseSlice = SliceImpl(content, pageable, slice.hasNext())
        return ResponseEntity.ok(responseSlice)
    }
}
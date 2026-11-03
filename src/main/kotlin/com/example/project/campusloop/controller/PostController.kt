package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.PostResponse
import com.example.project.campusloop.domain.entity.Post
import com.example.project.campusloop.domain.mapper.PostMapper
import com.example.project.campusloop.domain.request.CreatePostRequest
import com.example.project.campusloop.domain.request.EditPostRequest
import com.example.project.campusloop.service.PostService
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
@RequestMapping("/api/v1/groups/{groupId}/posts")
class PostController(
    private val service: PostService,
    private val mapper: PostMapper
) {

    @PostMapping
    fun createPost(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @RequestBody request: CreatePostRequest
    ): ResponseEntity<PostResponse> {
        val post = service.createPost(userId, groupId, mapper.toCommand(request))
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(post))
    }

    @PutMapping("/{postId}")
    fun editPost(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID,
        @RequestBody request: EditPostRequest
    ): ResponseEntity<PostResponse> {
        val post = service.editPost(userId, groupId, postId, mapper.toCommand(request))
        return ResponseEntity.ok(mapper.toResponse(post))
    }

    @DeleteMapping("/{postId}")
    fun deletePost(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID
    ): ResponseEntity<Void> {
        service.deletePost(userId, postId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{postId}")
    fun getPost(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable postId: UUID
    ): ResponseEntity<PostResponse> {
        val post = service.getPost(userId, postId)
        return ResponseEntity.ok(mapper.toResponse(post))
    }

    @GetMapping
    fun getPosts(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<PostResponse>> {
        val slice = service.getPosts(userId, groupId, pageable)
        val content = slice.content.map { mapper.toResponse(it) }
        val responseSlice = SliceImpl(content, pageable, slice.hasNext())
        return ResponseEntity.ok(responseSlice)
    }
}
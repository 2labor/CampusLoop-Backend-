package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.StoryMapMarkerResponse
import com.example.project.campusloop.domain.dto.StoryResponse
import com.example.project.campusloop.domain.mapper.StoryMapper
import com.example.project.campusloop.domain.request.CreateStoryRequest
import com.example.project.campusloop.domain.request.GetStoriesInRadiusRequest
import com.example.project.campusloop.service.StoriesService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/stories")
class StoryController (
    private val service: StoriesService,
    private val mapper: StoryMapper
) {

    @PostMapping
    fun createStory(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: CreateStoryRequest
    ): ResponseEntity<StoryResponse> {
        val command = mapper.toCommand(request)

        val story = service.createStory(userId, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(story))
    }

    @DeleteMapping("/{storyId}")
    fun deleteStory(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable storyId: UUID
    ): ResponseEntity<Void> {
        service.deleteStory(userId, storyId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{storyId}")
    fun getStory(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable storyId: UUID
    ): ResponseEntity<StoryResponse> {
        val story = service.getStory(userId, storyId)

        return ResponseEntity.ok(mapper.toResponse(story))
    }

    @GetMapping("/nearby")
    fun getStoriesInRadius(
        @AuthenticationPrincipal userId: UUID,
        @ModelAttribute request: GetStoriesInRadiusRequest
    ): ResponseEntity<List<StoryMapMarkerResponse>> {
        val command = mapper.toCommand(request)

        val storiesList = service.getStoryInRadius(userId, command)
        return ResponseEntity.ok(mapper.toMapResponseList(storiesList))
    }

    @GetMapping("/user/{targetUserId}")
    fun getUserStories(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable targetUserId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<StoryResponse>> {
        val storiesSlice = service.getUsersStories(userId, targetUserId, pageable)

        return ResponseEntity.ok(mapper.toSliceResponse(storiesSlice))
    }
}
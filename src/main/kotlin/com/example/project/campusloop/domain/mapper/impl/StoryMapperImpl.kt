package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.CreateStoryCommand
import com.example.project.campusloop.domain.command.GetStoriesInRadiusCommand
import com.example.project.campusloop.domain.dto.StoryMapMarkerResponse
import com.example.project.campusloop.domain.dto.StoryResponse
import com.example.project.campusloop.domain.entity.Story
import com.example.project.campusloop.domain.mapper.StoryMapper
import com.example.project.campusloop.domain.request.CreateStoryRequest
import com.example.project.campusloop.domain.request.GetStoriesInRadiusRequest
import com.example.project.campusloop.infra.storage.service.StorageService
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
class StoryMapperImpl (
    private val storageService: StorageService
) : StoryMapper {

    override fun toMapResponseList(storiesList  : List<Story>): List<StoryMapMarkerResponse> {
        return storiesList.map { story ->
            val resolvedUrl = resolveMediaUrl(story.mediaFileKey)
            toMapResponse(story, resolvedUrl)
        }
    }

    override fun toSliceResponse(storiesSlice: Slice<Story>): Slice<StoryResponse> {
        return storiesSlice.map { story ->
            toResponse(story)
        }
    }

    override fun toResponse(story: Story): StoryResponse {
        val resolvedUrl = resolveMediaUrl(story.mediaFileKey)

        return StoryResponse(
            id = story.id!!,
            mediaUrl = resolvedUrl,
            caption = story.caption,
            authorId = story.author.id!!,
            authorName = story.author.username,
            privacySetting = story.privacySettings,
            createdAt = story.createdAt,
        )
    }

    override fun toCommand(request: CreateStoryRequest): CreateStoryCommand {
        return CreateStoryCommand(
            mediaFileKey = request.mediaFileKey,
            latitude = request.latitude,
            longitude = request.longitude,
            caption = request.caption,
            privacy = request.privacy,
        )
    }

    override fun toCommand(request: GetStoriesInRadiusRequest): GetStoriesInRadiusCommand {
        return GetStoriesInRadiusCommand(
            latitude = request.latitude,
            longitude = request.longitude,
            radiusInMeters = request.radiusInMeters
        )
    }

    private fun toMapResponse(story: Story, mediaUrl: String): StoryMapMarkerResponse {
        return StoryMapMarkerResponse(
            storyId = story.id!!,
            authorId = story.author.id!!,
            authorName = story.author.username,
            latitude = story.latitude,
            longitude = story.longitude,
            mediaUrl = mediaUrl,
            caption = story.caption,
            createdAt = story.createdAt,
        )
    }

    private fun resolveMediaUrl(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            storageService.getFileUrl(url)
        }
    }
}
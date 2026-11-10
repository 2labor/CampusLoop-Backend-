package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.CreateStoryCommand
import com.example.project.campusloop.domain.command.GetStoriesInRadiusCommand
import com.example.project.campusloop.domain.dto.StoryMapMarkerResponse
import com.example.project.campusloop.domain.dto.StoryResponse
import com.example.project.campusloop.domain.entity.Story
import com.example.project.campusloop.domain.request.CreateStoryRequest
import com.example.project.campusloop.domain.request.GetStoriesInRadiusRequest
import org.springframework.data.domain.Slice

interface StoryMapper {
    fun toMapResponseList(storiesList: List<Story>): List<StoryMapMarkerResponse>
    fun toSliceResponse(storiesSlice: Slice<Story>): Slice<StoryResponse>
    fun toResponse(story: Story): StoryResponse
    fun toCommand(request: CreateStoryRequest): CreateStoryCommand
    fun toCommand(request: GetStoriesInRadiusRequest): GetStoriesInRadiusCommand
}
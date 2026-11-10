package com.example.project.campusloop.service

import com.example.project.campusloop.domain.command.CreateStoryCommand
import com.example.project.campusloop.domain.command.GetStoriesInRadiusCommand
import com.example.project.campusloop.domain.entity.Story
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface StoriesService {
    fun createStory(userId: UUID, command: CreateStoryCommand): Story
    fun deleteStory(operatorId: UUID, storyId: UUID)
    fun getStory(operatorId: UUID, storyId: UUID): Story
    fun getStoryInRadius(operatorId: UUID, command: GetStoriesInRadiusCommand): List<Story>
    fun getUsersStories(operatorId: UUID, userId: UUID, pageable: Pageable): Slice<Story>
}
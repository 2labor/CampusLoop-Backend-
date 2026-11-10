package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.CreateStoryCommand
import com.example.project.campusloop.domain.command.GetStoriesInRadiusCommand
import com.example.project.campusloop.domain.entity.Story
import com.example.project.campusloop.domain.enum.StoriesPrivacy
import com.example.project.campusloop.infra.geo.reposotiry.RedisGeoStoriesRepository
import com.example.project.campusloop.infra.storage.service.StorageService
import com.example.project.campusloop.repository.StoriesRepository
import com.example.project.campusloop.service.FriendshipService
import com.example.project.campusloop.service.StoriesService
import com.example.project.campusloop.service.UserService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class StoriesServiceImpl (
    private val repository: StoriesRepository,
    private val locationRepository: RedisGeoStoriesRepository,
    private val storageService: StorageService,
    private val friendshipService: FriendshipService,
    private val userService: UserService,
) : StoriesService {

    @Transactional
    override fun createStory(
        userId: UUID,
        command: CreateStoryCommand
    ): Story {
        val user = userService.getUserById(userId) ?:
            throw IllegalArgumentException("User `$userId` does not exist")

        val story = Story(
            mediaFileKey = command.mediaFileKey,
            caption = command.caption,
            author = user,
            privacySettings = command.privacy,
            latitude = command.latitude,
            longitude = command.longitude,
        )

        val savedStory = repository.save(story)

        locationRepository.addStoryLocation(
            savedStory.id!!,
            latitude = savedStory.latitude,
            longitude = savedStory.longitude,
        )

        return savedStory
    }

    override fun deleteStory(operatorId: UUID, storyId: UUID) {
        val story = repository.findByIdOrNull(storyId) ?:
            throw IllegalArgumentException("Story `$storyId` does not exist")

        if (story.author.id != operatorId) throw IllegalArgumentException("You cannot delete massage that does not belong to you")

        locationRepository.removeStoryLocation(storyId)
        repository.delete(story)

        runCatching {
            storageService.deleteFile(story.mediaFileKey)
        }
    }

    override fun getStory(
        operatorId: UUID,
        storyId: UUID
    ): Story {
        val story = repository.findByIdOrNull(storyId)
            ?: throw IllegalArgumentException("Story `$storyId` does not exist")

        if (story.expiresAt.isBefore(Instant.now())) {
            throw IllegalStateException("Story has expired")
        }

        if (!canViewStory(operatorId, story)) {
            throw IllegalArgumentException("You cannot access to view this story")
        }

        return story
    }

    override fun getStoryInRadius(
        operatorId: UUID,
        command: GetStoriesInRadiusCommand
    ): List<Story> {
        val storyIds = locationRepository.findStoriesInRadius(command.latitude, command.longitude, command.radiusInMeters)
        if (storyIds.isEmpty()) return emptyList()

        val activeStories = repository.findActiveStoriesByIds(storyIds, Instant.now())

        return activeStories.filter { story -> canViewStory(operatorId, story) }
    }

    override fun getUsersStories(
        operatorId: UUID,
        userId: UUID,
        pageable: Pageable
    ): Slice<Story> {
        val storiesSlice = repository.findActiveStoriesByAuthorId(userId, Instant.now() , pageable)

        if (storiesSlice.isEmpty) return SliceImpl(emptyList())

        return SliceImpl(
            storiesSlice.content.filter { story -> canViewStory(operatorId, story) },
            storiesSlice.pageable,
            storiesSlice.hasNext()
        )
    }

    private fun canViewStory(operatorId: UUID, story: Story): Boolean {
        val authorId = story.author.id!!

        return operatorId == authorId || when(story.privacySettings) {
            StoriesPrivacy.EVERYONE -> return true
            StoriesPrivacy.FRIENDS -> friendshipService.isFriends(operatorId, authorId)
            StoriesPrivacy.ONLY_ME -> return false
        }
    }
}
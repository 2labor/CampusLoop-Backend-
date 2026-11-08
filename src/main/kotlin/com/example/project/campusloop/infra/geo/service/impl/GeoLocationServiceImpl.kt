package com.example.project.campusloop.infra.geo.service.impl

import com.example.project.campusloop.infra.geo.domain.command.UpdateLocationCommand
import com.example.project.campusloop.infra.geo.domain.dto.UserLocationDto
import com.example.project.campusloop.infra.geo.reposotiry.RedisGeoRepository
import com.example.project.campusloop.infra.geo.service.GeoLocationService
import com.example.project.campusloop.infra.websocket.realtime.RealtimePublisher
import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import com.example.project.campusloop.service.FriendshipService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GeoLocationServiceImpl (
    private val repository: RedisGeoRepository,
    private val friendService: FriendshipService,
    private val realtimePublisher: RealtimePublisher
): GeoLocationService {
    override fun updateLocation(
        userId: UUID,
        command: UpdateLocationCommand
    ) {
        repository.updateUserLocation(userId, command.latitude, command.longitude)

        val locationDto = UserLocationDto(
            userId = userId,
            latitude = command.latitude,
            longitude = command.longitude
        )

        val friendIds = getFriendIds(userId)

        friendIds.forEach { friendId ->
            realtimePublisher.sendToUser(
                userId = friendId,
                destination = "/queue/geo/friends",
                action = RealtimeAction.UPDATED,
                entityId = userId,
                payload = locationDto
            )
        }
    }

    override fun enableGhostMode(userId: UUID) {
        repository.removeUserLocation(userId)

        val friendIds = getFriendIds(userId)
        friendIds.forEach { friendId ->
            realtimePublisher.sendToUser(
                userId = friendId,
                destination = "/queue/geo/friends",
                action = RealtimeAction.DELETED,
                entityId = userId,
                payload = null
            )
        }
    }

    override fun getFriendsLocations(
        userId: UUID,
        radiusInMeters: Double
    ): List<UserLocationDto> {
        val friendIds = getFriendIds(userId).toSet()
        if (friendIds.isEmpty()) return emptyList()

        val userLocation = repository.getUserLocation(userId) ?: return emptyList()

        val nearbyLocations = repository.findUsersInRadius(
            latitude = userLocation.latitude,
            longitude = userLocation.longitude,
            radiusInMeters = radiusInMeters,
            limit = 100
        )

        return nearbyLocations.filter { friendIds.contains(it.userId) && it.userId != userId }
    }

    private fun getFriendIds(
        userId: UUID
    ): List<UUID> {
        val friendshipsSlice = friendService.getFriends(userId, Pageable.unpaged())

        return friendshipsSlice.content.mapNotNull { friendship ->
            val initializerId = friendship.initializer.id
            val targetId = friendship.target.id

            when (userId) {
                initializerId -> targetId
                targetId -> initializerId
                else -> null
            }
        }
    }
}
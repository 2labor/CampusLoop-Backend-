package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Friendship
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface FriendshipService {
    fun sendFriendRequest(operatorId: UUID, targetId: UUID): Friendship
    fun acceptFriendRequest(operatorId: UUID, requesterId: UUID)
    fun rejectFriendRequest(operatorId: UUID, requesterId: UUID)
    fun removeFriendOrCancelRequest(operatorId: UUID, targetId: UUID)
    fun blockUser(operatorId: UUID, targetId: UUID)
    fun unblockUser(operatorId: UUID, targetId: UUID)
    fun getIncomingRequestsCount(currentId: UUID): Long
    fun getOutgoingRequests(currentId: UUID, pageable: Pageable): Slice<Friendship>
    fun getIncomingRequests(currentId: UUID, pageable: Pageable): Slice<Friendship>
    fun getFriends(currentId: UUID, pageable: Pageable): Slice<Friendship>
}
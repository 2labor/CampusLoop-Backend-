package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Friendship
import com.example.project.campusloop.domain.enum.FriendshipStatus
import com.example.project.campusloop.infra.websocket.realtime.service.FriendshipDispatchService
import com.example.project.campusloop.repository.FriendshipRepository
import com.example.project.campusloop.repository.UserRepository
import com.example.project.campusloop.service.FriendshipService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class FriendshipServiceImpl (
    private val repository: FriendshipRepository,
    private val userRepository: UserRepository,
    private val dispatchService: FriendshipDispatchService
): FriendshipService {

    @Transactional
    override fun sendFriendRequest(
        operatorId: UUID,
        targetId: UUID
    ): Friendship {
        if (operatorId == targetId) {
            throw IllegalArgumentException("You cannot send a friend request to yourself")
        }

        val currentUser = userRepository.findById(operatorId)
            .orElseThrow { IllegalArgumentException("No user with id $operatorId") }
        val targetUser = userRepository.findById(targetId)
            .orElseThrow { IllegalArgumentException("No user with id $targetId") }

        val existingConnection = repository.findBetweenUsers(currentUser, targetUser)

        if (existingConnection.isPresent) {
            val connection = existingConnection.get()
            when (connection.status) {
                FriendshipStatus.BLOCKED -> throw IllegalStateException("Action prohibited due to block status")
                FriendshipStatus.ACCEPTED -> throw IllegalStateException("You are already in friends")
                FriendshipStatus.PENDING -> {
                    if (connection.target.id == operatorId) {
                        connection.status = FriendshipStatus.ACCEPTED
                        return repository.save(connection)
                    } else {
                        throw IllegalStateException("Request already sent")
                    }
                }
            }
        }

        val newRequest = Friendship(
            initializer = currentUser,
            target = targetUser,
            status = FriendshipStatus.PENDING,
        )

        val saved = repository.save(newRequest)

        dispatchService.notifyFriendRequestSent(saved)

        return saved
    }

    @Transactional
    override fun acceptFriendRequest(operatorId: UUID, requesterId: UUID) {
        val request = findPendingRequest(operatorId, requesterId)

        request.status = FriendshipStatus.ACCEPTED
        val saved = repository.save(request)

        dispatchService.notifyFriendRequestAccepted(saved)
    }

    @Transactional
    override fun rejectFriendRequest(operatorId: UUID, requesterId: UUID) {
        val request = findPendingRequest(operatorId, requesterId)

        repository.delete(request)

        dispatchService.notifyFriendshipRemoved(request, requesterId)
    }

    @Transactional
    override fun removeFriendOrCancelRequest(operatorId: UUID, targetId: UUID) {
        val currentUser = userRepository.findById(operatorId)
            .orElseThrow { IllegalArgumentException("No user with id $operatorId") }
        val targetUser = userRepository.findById(targetId)
            .orElseThrow { IllegalArgumentException("No user with id $targetId") }

        val connection = repository.findBetweenUsers(currentUser, targetUser)
            .orElseThrow { IllegalArgumentException("No connection between users") }

        if (connection.status != FriendshipStatus.ACCEPTED) {
            throw IllegalStateException("You are not in friends")
        }

        repository.delete(connection)

        dispatchService.notifyFriendshipRemoved(connection, targetId)
    }

    @Transactional
    override fun blockUser(operatorId: UUID, targetId: UUID) {
        val currentUser = userRepository.findById(operatorId)
            .orElseThrow { IllegalArgumentException("No user with id $operatorId") }
        val targetUser = userRepository.findById(targetId)
            .orElseThrow { IllegalArgumentException("No user with id $targetId") }

        repository.findBetweenUsers(currentUser, targetUser).ifPresent {
            repository.delete(it)
        }

        val blockRecord = Friendship(
            initializer = currentUser,
            target = targetUser,
            status = FriendshipStatus.BLOCKED
        )
        val saved = repository.save(blockRecord)

        dispatchService.notifyUserBlocked(saved)
    }

    @Transactional
    override fun unblockUser(operatorId: UUID, targetId: UUID) {
        val currentUser = userRepository.findById(operatorId)
            .orElseThrow { IllegalArgumentException("Current user not found") }
        val targetUser = userRepository.findById(targetId)
            .orElseThrow { IllegalArgumentException("Target user not found") }

        val connection = repository.findByInitializerAndTarget(currentUser, targetUser)
            .orElseThrow { IllegalArgumentException("Block record not found") }

        check(connection.status == FriendshipStatus.BLOCKED) { "User is not blocked by you" }

        repository.delete(connection)    }

    @Transactional(readOnly = true)
    override fun getIncomingRequestsCount(currentId: UUID): Long {
        val currentUser = userRepository.findById(currentId)
            .orElseThrow { IllegalArgumentException("Current user not found") }
        return repository.countByTargetAndStatus(currentUser, FriendshipStatus.PENDING)
    }

    @Transactional(readOnly = true)
    override fun getOutgoingRequests(currentId: UUID, pageable: Pageable): Slice<Friendship> {
        val currentUser = userRepository.findById(currentId)
            .orElseThrow { IllegalArgumentException("Current user not found") }
        return repository.findByInitializerAndStatus(currentUser, FriendshipStatus.PENDING, pageable)
    }

    @Transactional(readOnly = true)
    override fun getIncomingRequests(currentId: UUID, pageable: Pageable): Slice<Friendship> {
        val currentUser = userRepository.findById(currentId)
            .orElseThrow { IllegalArgumentException("Current user not found") }
        return repository.findByTargetAndStatus(currentUser, FriendshipStatus.PENDING, pageable)
    }

    @Transactional(readOnly = true)
    override fun getFriends(currentId: UUID, pageable: Pageable): Slice<Friendship> {
        val currentUser = userRepository.findById(currentId)
            .orElseThrow { IllegalArgumentException("Current user not found") }
        return repository.findAllAcceptedConnections(currentUser, pageable)
    }

    private fun findPendingRequest(currentId: UUID, requesterId: UUID): Friendship {
        val currentUser = userRepository.findById(currentId)
            .orElseThrow { IllegalArgumentException("No user with id $currentId") }
        val targetUser = userRepository.findById(requesterId)
            .orElseThrow { IllegalArgumentException("No user with id $requesterId") }

        val request = repository.findBetweenUsers(currentUser, targetUser)
            .orElseThrow { IllegalArgumentException("No request between users") }

        if (request.status != FriendshipStatus.PENDING) {
            throw IllegalStateException("Request is not in pending stage")
        }

        return request
    }
}
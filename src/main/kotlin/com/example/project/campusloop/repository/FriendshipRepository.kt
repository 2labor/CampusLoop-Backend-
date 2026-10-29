package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Friendship
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.enum.FriendshipStatus
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface FriendshipRepository : JpaRepository<Friendship, UUID> {
    fun findByInitializerAndTarget(initializer: User, target: User): Optional<Friendship>

    @Query("""
        SELECT f FROM Friendship f 
        WHERE (f.initializer = :user1 AND f.target = :user2) 
           OR (f.initializer = :user2 AND f.target = :user1)
    """)
    fun findBetweenUsers(@Param("user1") user1: User, @Param("user2") user2: User): Optional<Friendship>

    fun findByTargetAndStatus(target: User, status: FriendshipStatus, pageable: Pageable): Slice<Friendship>

    fun findByInitializerAndStatus(initializer: User, status: FriendshipStatus, pageable: Pageable): Slice<Friendship>

    @Query("""
        SELECT f FROM Friendship f 
        WHERE (f.initializer = :user OR f.target = :user) 
          AND f.status = 'ACCEPTED'
    """)
    fun findAllAcceptedConnections(@Param("user") user: User, pageable: Pageable): Slice<Friendship>

    fun countByTargetAndStatus(target: User, status: FriendshipStatus): Long
}
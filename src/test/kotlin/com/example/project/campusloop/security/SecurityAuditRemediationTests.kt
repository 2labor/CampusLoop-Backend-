package com.example.project.campusloop.security

import com.example.project.campusloop.domain.entity.Friendship
import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.GroupMember
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.enum.FriendshipStatus
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.infra.websocket.JwtChannelInterceptor
import com.example.project.campusloop.infra.websocket.realtime.service.FriendshipDispatchService
import com.example.project.campusloop.repository.FriendshipRepository
import com.example.project.campusloop.repository.GroupMemberRepository
import com.example.project.campusloop.repository.GroupRepository
import com.example.project.campusloop.repository.UserRepository
import com.example.project.campusloop.service.UserService
import com.example.project.campusloop.service.impl.AuthFacade
import com.example.project.campusloop.service.impl.FriendshipServiceImpl
import com.example.project.campusloop.service.impl.GroupMemberServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.data.domain.PageRequest
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.Optional
import java.util.UUID

class SecurityAuditRemediationTests {

    @Test
    fun `SEC-04 university email validation enforces strict domain boundary`() {
        assertTrue(AuthFacade.isAllowedUniversityEmail("student@tudublin.ie", "tudublin.ie"))
        assertTrue(AuthFacade.isAllowedUniversityEmail("student@mytudublin.tudublin.ie", "tudublin.ie"))
        assertTrue(AuthFacade.isAllowedUniversityEmail("STUDENT@TUDUBLIN.IE", "@tudublin.ie"))

        // Lookalike suffix domain must be rejected
        assertFalse(AuthFacade.isAllowedUniversityEmail("attacker@evil-tudublin.ie", "tudublin.ie"))
        assertFalse(AuthFacade.isAllowedUniversityEmail("attacker@tudublin.ie.evil.com", "tudublin.ie"))
        assertFalse(AuthFacade.isAllowedUniversityEmail("@tudublin.ie", "tudublin.ie"))
    }

    @Test
    fun `SEC-07 JwtCore rejects weak secrets and validates issuer`() {
        assertThrows(IllegalArgumentException::class.java) {
            JwtCore("too-short-secret", 60000L, "campusloop-api")
        }

        val secret = "0123456789abcdef0123456789abcdef-strong-secret-key"
        val jwtCoreA = JwtCore(secret, 60000L, "campusloop-api")
        val jwtCoreB = JwtCore(secret, 60000L, "other-issuer")

        val userId = UUID.randomUUID()
        val token = jwtCoreA.generateToken(userId)

        assertTrue(jwtCoreA.validToken(token))
        assertEquals(userId, jwtCoreA.getUserIdFromToken(token))
        assertFalse(jwtCoreB.validToken(token))
    }

    @Test
    fun `SEC-03 JwtChannelInterceptor blocks cross-user STOMP topic subscription`() {
        val secret = "0123456789abcdef0123456789abcdef-strong-secret-key"
        val jwtCore = JwtCore(secret, 60000L, "campusloop-api")
        val interceptor = JwtChannelInterceptor(jwtCore)
        val channel = mock(MessageChannel::class.java)

        val userA = UUID.randomUUID()
        val userB = UUID.randomUUID()

        // User A subscribing to own topic succeeds
        val ownAccessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE)
        ownAccessor.user = UsernamePasswordAuthenticationToken(userA, null, emptyList())
        ownAccessor.destination = "/topic/users/$userA/friends"
        val ownMsg = MessageBuilder.createMessage(ByteArray(0), ownAccessor.messageHeaders)
        interceptor.preSend(ownMsg, channel)

        // User A subscribing to User B's private topic is denied
        val crossAccessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE)
        crossAccessor.user = UsernamePasswordAuthenticationToken(userA, null, emptyList())
        crossAccessor.destination = "/topic/users/$userB/friends"
        val crossMsg = MessageBuilder.createMessage(ByteArray(0), crossAccessor.messageHeaders)

        assertThrows(AccessDeniedException::class.java) {
            interceptor.preSend(crossMsg, channel)
        }
    }

    @Test
    fun `SEC-02 requester cannot accept their own outgoing friend request`() {
        val friendshipRepo = mock(FriendshipRepository::class.java)
        val userRepo = mock(UserRepository::class.java)
        val dispatchService = mock(FriendshipDispatchService::class.java)
        val service = FriendshipServiceImpl(friendshipRepo, userRepo, dispatchService)

        val requesterId = UUID.randomUUID()
        val targetId = UUID.randomUUID()
        val requester = User(id = requesterId, username = "alice")
        val target = User(id = targetId, username = "bob")

        `when`(userRepo.findById(requesterId)).thenReturn(Optional.of(requester))
        `when`(userRepo.findById(targetId)).thenReturn(Optional.of(target))
        // Outgoing request was initialized by requester -> target, so reverse lookup (target -> requester) is empty
        `when`(friendshipRepo.findByInitializerAndTarget(target, requester)).thenReturn(Optional.empty())

        assertThrows(AccessDeniedException::class.java) {
            service.acceptFriendRequest(requesterId, targetId)
        }
    }

    @Test
    fun `SEC-09 private group members list rejects non-members`() {
        val memberRepo = mock(GroupMemberRepository::class.java)
        val userService = mock(UserService::class.java)
        val groupRepo = mock(GroupRepository::class.java)
        val service = GroupMemberServiceImpl(memberRepo, userService, groupRepo)

        val creatorId = UUID.randomUUID()
        val outsiderId = UUID.randomUUID()
        val groupId = UUID.randomUUID()

        val creator = User(id = creatorId, username = "creator")
        val outsider = User(id = outsiderId, username = "outsider")
        val privateGroup = Group(id = groupId, name = "Secret Lab", isPrivate = true, creator = creator)

        `when`(groupRepo.findById(groupId)).thenReturn(Optional.of(privateGroup))
        `when`(userService.getUserById(outsiderId)).thenReturn(outsider)
        `when`(memberRepo.findByGroupAndUser(privateGroup, outsider)).thenReturn(Optional.empty())

        assertThrows(AccessDeniedException::class.java) {
            service.getGroupMembers(outsiderId, groupId, PageRequest.of(0, 10))
        }
    }
}

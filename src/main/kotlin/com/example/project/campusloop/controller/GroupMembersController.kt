package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.GroupMemberResponse
import com.example.project.campusloop.domain.dto.toResponse
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.service.GroupMemberService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/groups")
class GroupMembersController (
    private val service: GroupMemberService
) {

    @PostMapping("{groupId}/join")
    fun joinGroup(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
    ): ResponseEntity<GroupMemberResponse> {
        val membership = service.joinGroup(groupId, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(membership.toResponse())
    }

    @DeleteMapping("{groupId}/leave")
    fun leaveGroup(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
    ): ResponseEntity<Void> {
        service.leaveGroup(groupId, userId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{groupId}/members")
    fun getMembers(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        pageable: Pageable
    ):ResponseEntity<Slice<GroupMemberResponse>> {
        val slice = service.getGroupMembers(userId, groupId, pageable)
        val content = slice.content.map { it.toResponse() }
        val responseSlice = SliceImpl(content, pageable, slice.hasNext())
        return ResponseEntity.ok(responseSlice)
    }

    @PostMapping("/{groupId}/members/{targetId}/role")
    fun updateMemberRole(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable targetId: UUID,
        @RequestBody role: GroupMemberRole,
    ): ResponseEntity<GroupMemberResponse> {
        val updated = service.updateMemberRole(userId, groupId, targetId, role)
        return ResponseEntity.ok(updated.toResponse())
    }

    @PostMapping("/{groupId}/members/{targetId}/status")
    fun updateMemberStatus(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @PathVariable targetId: UUID,
        @RequestBody status: GroupMemberStatus,
    ): ResponseEntity<GroupMemberResponse> {
        val updated = service.updateMemberStatus(userId, groupId, targetId, status)
        return ResponseEntity.ok(updated.toResponse())
    }
}
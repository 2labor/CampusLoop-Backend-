package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.GroupResponse
import com.example.project.campusloop.domain.dto.toResponse
import com.example.project.campusloop.domain.request.CreateGroupRequest
import com.example.project.campusloop.domain.request.UpdateGroupRequest
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.GroupService
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/groups")
class GroupController (
    private val service: GroupService,
    private val memberService: GroupMemberService,
) {

    @PostMapping
    fun createGroup(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: CreateGroupRequest
    ): ResponseEntity<GroupResponse> {
        val group = service.createGroup(userId, request.name, request.description, request.isPrivate)
        val count = memberService.getMemberCount(group.id!!)
        return ResponseEntity.status(HttpStatus.CREATED).body(group.toResponse(count))
    }

    @GetMapping("/{groupId}")
    fun getGroupById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID
    ): ResponseEntity<GroupResponse> {
        val group = service.getGroupById(groupId)
        val count = memberService.getMemberCount(group.id!!)
        return ResponseEntity.ok(group.toResponse(count))
    }

    @GetMapping
    fun getGroups(
        pageable: Pageable
    ): ResponseEntity<Slice<GroupResponse>> {
        val slice = service.getGroups(pageable)
        val content = slice.content.map { group ->
            val count = memberService.getMemberCount(group.id!!)
            group.toResponse(count)
        }
        val responseSlice = SliceImpl(content, pageable, slice.hasNext())

        return ResponseEntity.ok(responseSlice)
    }

    @GetMapping("/search")
    fun searchGroups(
        @RequestParam query: String,
        pageable: Pageable
    ): ResponseEntity<Slice<GroupResponse>> {
        val slice = service.searchGroup(query, pageable)
        val content = slice.content.map { group ->
            val count = memberService.getMemberCount(group.id!!)
            group.toResponse(count)
        }

        val responseSlice = SliceImpl(content, pageable, slice.hasNext())

        return ResponseEntity.ok(responseSlice)
    }

    @PutMapping("/{groupId}")
    fun updateGroup(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        @RequestBody request: UpdateGroupRequest
    ): ResponseEntity<GroupResponse> {
        val updatedGroup = service.updateGroup(userId, groupId, request.name, request.description, request.isPrivate)
        val count = memberService.getMemberCount(groupId)
        return ResponseEntity.ok(updatedGroup.toResponse(count))
    }

    @DeleteMapping("/{groupId}")
    fun deleteGroup(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID
    ): ResponseEntity<Void> {
        service.deleteGroup(userId, groupId)
        return ResponseEntity.noContent().build()
    }
}
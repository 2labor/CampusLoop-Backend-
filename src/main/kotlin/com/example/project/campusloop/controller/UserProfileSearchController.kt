package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.UserProfileResponse
import com.example.project.campusloop.service.UserProfileSearchService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserProfileSearchController (
    private val service: UserProfileSearchService
) {

    @GetMapping("/search")
    fun searchUsers(
        @RequestParam query: String,
        pageable: Pageable
    ): ResponseEntity<Slice<UserProfileResponse>> {
        val response = service.searchUsers(query, pageable)
        return ResponseEntity.ok(response)
    }

}
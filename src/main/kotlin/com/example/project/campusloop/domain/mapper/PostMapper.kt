package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.CreatePostCommand
import com.example.project.campusloop.domain.command.EditPostCommand
import com.example.project.campusloop.domain.dto.PostResponse
import com.example.project.campusloop.domain.entity.Post
import com.example.project.campusloop.domain.request.CreatePostRequest
import com.example.project.campusloop.domain.request.EditPostRequest

interface PostMapper {
    fun toResponse(entity: Post): PostResponse
    fun toCommand(request: CreatePostRequest): CreatePostCommand
    fun toCommand(request: EditPostRequest): EditPostCommand
}
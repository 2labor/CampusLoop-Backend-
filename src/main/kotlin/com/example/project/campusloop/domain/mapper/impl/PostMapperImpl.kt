package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.CreatePostCommand
import com.example.project.campusloop.domain.command.EditPostCommand
import com.example.project.campusloop.domain.dto.AuthorDto
import com.example.project.campusloop.domain.dto.PostResponse
import com.example.project.campusloop.domain.entity.Post
import com.example.project.campusloop.domain.mapper.PostMapper
import com.example.project.campusloop.domain.request.CreatePostRequest
import com.example.project.campusloop.domain.request.EditPostRequest
import org.springframework.stereotype.Component

@Component
class PostMapperImpl : PostMapper {
    override fun toResponse(entity: Post): PostResponse {
        return PostResponse(
            id = entity.id!!,
            title = entity.title,
            content = entity.content,
            groupId = entity.group.id!!,
            isAnonymous = entity.isAnonymous,
            author = if (entity.isAnonymous) null else
                AuthorDto(id = entity.author.id!!, username = entity.author.username),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    override fun toCommand(request: CreatePostRequest): CreatePostCommand {
        return CreatePostCommand(
            title = request.title,
            content = request.content,
            isAnonymous = request.isAnonymous,
        )
    }

    override fun toCommand(request: EditPostRequest): EditPostCommand {
        return EditPostCommand(
            title = request.title,
            content = request.content,
            isAnonymous = request.isAnonymous
        )
    }
}
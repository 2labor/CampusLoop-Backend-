package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.CreateCommentCommand
import com.example.project.campusloop.domain.command.EditCommentCommand
import com.example.project.campusloop.domain.dto.AuthorDto
import com.example.project.campusloop.domain.dto.CommentResponse
import com.example.project.campusloop.domain.entity.Comment
import com.example.project.campusloop.domain.mapper.CommentMapper
import com.example.project.campusloop.domain.request.CreateCommentRequest
import com.example.project.campusloop.domain.request.EditCommentRequest
import org.springframework.stereotype.Component

@Component
class CommentMapperImpl : CommentMapper {
    override fun toResponse(entity: Comment): CommentResponse {
        return CommentResponse(
            id = entity.id!!,
            content = entity.content,
            parentId = entity.parent?.id,
            author = AuthorDto(entity.author.id!!, entity.author.username),
            postId = entity.post.id!!,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    override fun toCommand(request: CreateCommentRequest): CreateCommentCommand {
        return CreateCommentCommand(request.content, request.parentId)
    }

    override fun toCommand(request: EditCommentRequest): EditCommentCommand {
        return EditCommentCommand(request.content)
    }
}
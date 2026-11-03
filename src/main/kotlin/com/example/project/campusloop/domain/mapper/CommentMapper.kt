package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.command.CreateCommentCommand
import com.example.project.campusloop.domain.command.EditCommentCommand
import com.example.project.campusloop.domain.dto.CommentResponse
import com.example.project.campusloop.domain.entity.Comment
import com.example.project.campusloop.domain.request.CreateCommentRequest
import com.example.project.campusloop.domain.request.EditCommentRequest

interface CommentMapper {
    fun toResponse(entity: Comment): CommentResponse
    fun toCommand(request: CreateCommentRequest): CreateCommentCommand
    fun toCommand(request: EditCommentRequest): EditCommentCommand
}
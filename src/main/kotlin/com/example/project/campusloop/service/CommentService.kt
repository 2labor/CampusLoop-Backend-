package com.example.project.campusloop.service

import com.example.project.campusloop.domain.command.CreateCommentCommand
import com.example.project.campusloop.domain.command.EditCommentCommand
import com.example.project.campusloop.domain.entity.Comment
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface CommentService {
    fun createComment(authorId: UUID, groupId: UUID, postId: UUID, command: CreateCommentCommand): Comment
    fun editComment(operatorId: UUID, commentId: UUID, command: EditCommentCommand): Comment
    fun deleteComment(operatorId: UUID, postId: UUID, commentId: UUID)
    fun getComment(operatorId: UUID, commentId: UUID): Comment
    fun getCommentsByPost(operatorId: UUID, postId: UUID, pageable: Pageable): Slice<Comment>
}
package com.example.project.campusloop.service

import com.example.project.campusloop.domain.command.CreatePostCommand
import com.example.project.campusloop.domain.command.EditPostCommand
import com.example.project.campusloop.domain.entity.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface PostService {
    fun createPost(authorId: UUID, groupId: UUID, command: CreatePostCommand): Post
    fun editPost(operatorId: UUID, groupId: UUID, postId: UUID, command: EditPostCommand): Post
    fun deletePost(operatorId: UUID, postId: UUID)
    fun getPost(operatorId: UUID, postId: UUID): Post
    fun getPosts(operatorId: UUID, groupId: UUID, pageable: Pageable): Slice<Post>
}
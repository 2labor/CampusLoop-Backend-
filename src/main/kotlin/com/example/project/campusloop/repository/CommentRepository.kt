package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Comment
import com.example.project.campusloop.domain.entity.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CommentRepository : JpaRepository<Comment, UUID> {
    fun findAllByPostAndParentIsNullOrderByCreatedAtAsc(post: Post, pageable: Pageable): Slice<Comment>

    fun findAllByParentOrderByCreatedAtAsc(parent: Comment, pageable: Pageable): Slice<Comment>
}
package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostRepository : JpaRepository<Post, UUID> {
    fun findAllByGroupOrderByCreatedAtDesc(group: Group, pageable: Pageable): Slice<Post>
}
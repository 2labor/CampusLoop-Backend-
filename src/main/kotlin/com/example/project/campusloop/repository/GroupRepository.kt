package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Group
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface GroupRepository : JpaRepository<Group, UUID> {
    @Query("SELECT g FROM Group g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchByName(@Param("query") query: String, pageable: Pageable): Slice<Group>
}
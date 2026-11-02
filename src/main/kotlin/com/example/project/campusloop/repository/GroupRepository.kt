package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Group
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface GroupRepository : JpaRepository<Group, UUID> {
    fun findAllByIsPrivateFalse(pageable: Pageable): Slice<Group>

    @Query("SELECT g FROM Group g WHERE g.isPrivate = false AND LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchByName(@Param("query") query: String, pageable: Pageable): Slice<Group>
}

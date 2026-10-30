package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.GroupMember
import com.example.project.campusloop.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface GroupMemberRepository : JpaRepository<GroupMember, UUID> {
    fun findByGroupAndUser(group: Group, user: User): Optional<GroupMember>
    fun findAllByGroup(group: Group, pageable: Pageable): Slice<GroupMember>
    fun findAllByUser(user: User, pageable: Pageable): Slice<GroupMember>
}
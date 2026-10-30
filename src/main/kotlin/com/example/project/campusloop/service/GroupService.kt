package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.GroupMember
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface GroupService {
    fun createGroup(operatorId: UUID, name: String, description: String? = null, isPrivate: Boolean = false): Group
    fun getGroupById(id: UUID): Group
    fun getGroups(pageable: Pageable): Slice<Group>
    fun searchGroup(query: String, pageable: Pageable): Slice<Group>
    fun updateGroup(operatorId: UUID, groupId: UUID, name: String?, description: String? = null, isPrivate: Boolean? = false): Group
    fun deleteGroup(operatorId: UUID, groupId: UUID)
}
package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.GroupMember
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface GroupMemberService {
    fun joinGroup(operatorId: UUID, groupId: UUID): GroupMember
    fun leaveGroup(operatorId: UUID, groupId: UUID)
    fun addMember(operatorId: UUID, groupId: UUID, targetId: UUID): GroupMember
    fun addToGroup(groupId: UUID, member: GroupMember)
    fun updateMemberRole(operatorId: UUID, groupId: UUID, targetId: UUID, newRole: GroupMemberRole): GroupMember
    fun updateMemberStatus(operatorId: UUID, groupId: UUID, targetId: UUID, newStatus: GroupMemberStatus): GroupMember
    fun getGroupMembers(operatorId: UUID, groupId: UUID, pageable: Pageable): Slice<GroupMember>
    fun getMemberCount(groupId: UUID): Long
    fun validateMemberRole(userId: UUID, group: Group, requiredRole: GroupMemberRole)
}
package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.GroupMember
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.repository.GroupRepository
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.GroupService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GroupServiceImpl (
    private val repository: GroupRepository,
    private val userService: UserService,
    private val memberService: GroupMemberService
) : GroupService {

    @Transactional
    override fun createGroup(
        operatorId: UUID,
        name: String,
        description: String?,
        isPrivate: Boolean
    ): Group {
        val creator = userService.getUserById(operatorId)
            ?: throw IllegalArgumentException("User with id $operatorId not found")

        val group = Group(
            name = name.trim(),
            description = description?.trim(),
            isPrivate = isPrivate,
            creator = creator
        )
        val savedGroup = repository.save(group)

        val adminMember = GroupMember(
            group = savedGroup,
            user = creator,
            role = GroupMemberRole.ADMIN,
            status = GroupMemberStatus.ACTIVE,
        )

        memberService.addToGroup(savedGroup.id!!, adminMember)

        return savedGroup
    }

    @Transactional(readOnly = true)
    override fun getGroupById(operatorId: UUID, id: UUID): Group {
        val group = repository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Group with id $id not found")

        if (group.isPrivate && group.creator.id != operatorId) {
            memberService.validateMemberRole(operatorId, group, GroupMemberRole.MEMBER)
        }

        return group
    }

    @Transactional(readOnly = true)
    override fun getGroups(pageable: Pageable): Slice<Group> {
        return repository.findAllByIsPrivateFalse(pageable)
    }

    @Transactional(readOnly = true)
    override fun searchGroup(
        query: String,
        pageable: Pageable
    ): Slice<Group> {
        return repository.searchByName(query.trim(), pageable)
    }

    @Transactional
    override fun updateGroup(
        operatorId: UUID,
        groupId: UUID,
        name: String?,
        description: String?,
        isPrivate: Boolean?
    ): Group {
        val group = repository.findByIdOrNull(groupId)
            ?: throw IllegalArgumentException("Group with id $groupId not found")

        memberService.validateMemberRole(operatorId, group, GroupMemberRole.ADMIN)

        name?.let { group.name = it.trim() }
        description?.let { group.description = it.trim() }
        isPrivate?.let { group.isPrivate = it }

        return repository.save(group)
    }

    @Transactional
    override fun deleteGroup(operatorId: UUID, groupId: UUID) {
        val group = repository.findByIdOrNull(groupId)
            ?: throw IllegalArgumentException("Group with id $groupId not found")

        if (group.creator.id != operatorId) {
            throw IllegalArgumentException("Only the creator can perform this action")
        }

        repository.delete(group)
    }
}

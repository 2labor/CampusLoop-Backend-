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
    private val repository : GroupRepository,
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
        val creator = userService.getUserById(operatorId) ?:
            throw  IllegalArgumentException("User with id $operatorId not found")

        val group = Group(
            name = name,
            description = description,
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
    override fun getGroupById(id: UUID): Group {
        return repository.findByIdOrNull(id) ?:
            throw  IllegalArgumentException("Group with id $id not found")
    }

    @Transactional(readOnly = true)
    override fun getGroups(pageable: Pageable): Slice<Group> {
        return repository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    override fun searchGroup(
        query: String,
        pageable: Pageable
    ): Slice<Group> {
        return repository.searchByName(query, pageable)
    }

    @Transactional
    override fun updateGroup(
        operatorId: UUID,
        groupId: UUID,
        name: String?,
        description: String?,
        isPrivate: Boolean?
    ): Group {
        val group = getGroupById(groupId)

        memberService.validateMemberRole(operatorId, group, GroupMemberRole.ADMIN)

        name?.let { group.name = it }
        description?.let { group.description = it }
        isPrivate?.let { group.isPrivate = it }

        return repository.save(group)
    }

    @Transactional
    override fun deleteGroup(operatorId: UUID, groupId: UUID) {
        val group = getGroupById(groupId)

        if (group.creator.id != operatorId) {
            throw IllegalArgumentException("Only the creator can perform this action")
        }

        repository.delete(group)
    }
}
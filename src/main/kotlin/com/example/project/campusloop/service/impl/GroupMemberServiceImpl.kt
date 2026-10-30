package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.GroupMember
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.repository.GroupMemberRepository
import com.example.project.campusloop.repository.GroupRepository
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GroupMemberServiceImpl (
    private val repository : GroupMemberRepository,
    private val userService: UserService,
    private val groupRepository: GroupRepository
) : GroupMemberService {

    @Transactional
    override fun joinGroup(
        operatorId: UUID,
        groupId: UUID
    ): GroupMember {
        val user = userService.getUserById(operatorId) ?:
            throw IllegalArgumentException("User with id $operatorId not found")
        val group = groupRepository.findById(groupId)
            .orElseThrow { IllegalArgumentException("Group with id $groupId not found") }

        val existingMembership = repository.findByGroupAndUser(group, user).orElse(null)

        if (existingMembership != null) {
            if (existingMembership.status == GroupMemberStatus.BANNED) {
                throw IllegalStateException("User is banned from this group")
            }
            return existingMembership
        }

        val initialStatus = if (group.isPrivate) GroupMemberStatus.PENDING else GroupMemberStatus.ACTIVE

        val newMembership = GroupMember(
            group = group,
            user = user,
            role = GroupMemberRole.MEMBER,
            status = initialStatus
        )

        return repository.save(newMembership)
    }

    @Transactional
    override fun leaveGroup(operatorId: UUID, groupId: UUID) {
        val user = userService.getUserById(operatorId) ?:
            throw IllegalArgumentException("User not found")
        val group = groupRepository.findById(groupId)
            .orElseThrow { IllegalArgumentException("Group not found") }

        val membership = repository.findByGroupAndUser(group, user)
            .orElseThrow { IllegalArgumentException("User is not a member of this group") }

        if (group.creator.id == operatorId) {
            throw IllegalStateException("Group creator cannot leave the group. Delete the group or transfer ownership instead.")
        }

        repository.delete(membership)
    }

    @Transactional
    override fun addMember(
        operatorId: UUID,
        groupId: UUID,
        targetId: UUID
    ): GroupMember {
        val user = userService.getUserById(operatorId) ?:
        throw IllegalArgumentException("User with id $operatorId not found")
        val group = groupRepository.findById(groupId)
            .orElseThrow { IllegalArgumentException("Group with id $groupId not found") }

        val existingMembership = repository.findByGroupAndUser(group, user).orElse(null)

        if (existingMembership != null) {
            if (existingMembership.status == GroupMemberStatus.BANNED) {
                throw IllegalStateException("User is banned from this group")
            }

            if (existingMembership.status != GroupMemberStatus.ACTIVE) {
                existingMembership.status = GroupMemberStatus.ACTIVE
                return repository.save(existingMembership)
            }
            return existingMembership
        }

        val newMembership = GroupMember(
            group = group,
            user = user,
            role = GroupMemberRole.MEMBER,
            status = GroupMemberStatus.ACTIVE
        )

        return repository.save(newMembership)
    }

    @Transactional
    override fun addToGroup(groupId: UUID, member: GroupMember) {
        repository.save(member)
    }

    @Transactional
    override fun updateMemberRole(
        operatorId: UUID,
        groupId: UUID,
        targetId: UUID,
        newRole: GroupMemberRole
    ): GroupMember {
        val group = groupRepository.findById(groupId)
            .orElseThrow { IllegalArgumentException("Group not found") }

        validateMemberRole(operatorId, group, GroupMemberRole.ADMIN)

        val targetUser = userService.getUserById(targetId) ?:
            throw IllegalArgumentException("Target user not found")

        val targetMembership = repository.findByGroupAndUser(group, targetUser)
            .orElseThrow { IllegalArgumentException("Target user is not a member of this group") }

        targetMembership.role = newRole
        return repository.save(targetMembership)
    }

    @Transactional
    override fun updateMemberStatus(
        operatorId: UUID,
        groupId: UUID,
        targetId: UUID,
        newStatus: GroupMemberStatus
    ): GroupMember {
        val group = groupRepository.findById(groupId)
            .orElseThrow { IllegalArgumentException("Group not found") }

        validateMemberRole(operatorId, group, GroupMemberRole.MODERATOR)

        val targetUser = userService.getUserById(targetId) ?:
            throw IllegalArgumentException("Target user not found")

        val targetMembership = repository.findByGroupAndUser(group, targetUser)
            .orElseThrow { IllegalArgumentException("Target user is not a member of this group") }

        targetMembership.status = newStatus
        return repository.save(targetMembership)
    }

    override fun getGroupMembers(
        operatorId: UUID,
        groupId: UUID,
        pageable: Pageable
    ): Slice<GroupMember> {
        val group = groupRepository.findByIdOrNull(groupId) ?:
            throw IllegalArgumentException("Group with id: ${groupId} not found")
        if (group.isPrivate) {
            val requesterMembership = repository.findByIdOrNull(operatorId) ?:
                throw  IllegalArgumentException("Group with id: ${operatorId} is private")
            if (requesterMembership.status != GroupMemberStatus.ACTIVE) {
                throw  IllegalArgumentException("You have to  be member of group: ${groupId}")
            }
        }

        return repository.findAllByGroup(group, pageable)
    }

    @Transactional(readOnly = true)
    override fun getMemberCount(groupId: UUID): Long {
        val group = groupRepository.findById(groupId)
            .orElseThrow { IllegalArgumentException("Group not found") }

        return repository.findAllByGroup(group, Pageable.unpaged()).content
            .count { it.status == GroupMemberStatus.ACTIVE }.toLong()
    }

    override fun validateMemberRole(
        userId: UUID,
        group: Group,
        requiredRole: GroupMemberRole
    ) {
        val user = userService.getUserById(userId) ?:
            throw IllegalArgumentException("User not found")

        val membership = repository.findByGroupAndUser(group, user)
            .orElseThrow { IllegalArgumentException("User is not a member of this group") }

        if (membership.status != GroupMemberStatus.ACTIVE) {
            throw IllegalStateException("User membership is not active")
        }

        val isCreator = group.creator.id == userId
        val isAdmin = membership.role == GroupMemberRole.ADMIN || isCreator
        val isModerator = membership.role == GroupMemberRole.MODERATOR || isAdmin

        when (requiredRole) {
            GroupMemberRole.ADMIN -> {
                if (!isAdmin) throw IllegalStateException("Administrator privileges required")
            }
            GroupMemberRole.MODERATOR -> {
                if (!isModerator) throw IllegalStateException("Moderator or Administrator privileges required")
            }
            GroupMemberRole.MEMBER -> {}
        }
    }
}
package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.CreatePostCommand
import com.example.project.campusloop.domain.command.EditPostCommand
import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.entity.Post
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.repository.PostRepository
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.GroupService
import com.example.project.campusloop.service.PostService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PostServiceImpl (
    private val repository: PostRepository,
    private val groupService: GroupService,
    private val memberService: GroupMemberService,
    private val userService: UserService,
) : PostService {

    @Transactional
    override fun createPost(
        authorId: UUID,
        groupId: UUID,
        command: CreatePostCommand
    ): Post {
        val group = groupService.getGroupById(groupId)
        val author = userService.getUserById(authorId) ?:
            throw IllegalArgumentException("User with id $authorId does not exist")

        memberService.validateMemberRole(
            authorId,
            group,
            GroupMemberRole.MODERATOR
        )

        val post = Post(
            title = command.title,
            content = command.content,
            group = group,
            author = author,
            isAnonymous = command.isAnonymous
        )

        return repository.save(post)
    }

    @Transactional
    override fun editPost(
        operatorId: UUID,
        groupId: UUID,
        postId: UUID,
        command: EditPostCommand
    ): Post {
        val post = getPost(operatorId, postId)
        val group = post.group

        memberService.validateMemberRole(operatorId, group, GroupMemberRole.MODERATOR)

        command.title?.let { post.title = it }
        command.content?.let { post.content = it }
        command.isAnonymous?.let { post.isAnonymous = it }

        return repository.save(post)
    }

    @Transactional
    override fun deletePost(operatorId: UUID, postId: UUID) {
        val post = getPost(operatorId, postId)
        val group = post.group

        memberService.validateMemberRole(operatorId, group, GroupMemberRole.MODERATOR)

        repository.delete(post)
    }

    @Transactional(readOnly = true)
    override fun getPost(
        operatorId: UUID,
        postId: UUID
    ): Post {
        val post = repository.findByIdOrNull(postId) ?:
            throw IllegalArgumentException("Post with id $postId does not exist")

        val group = post.group

        checkForMembership(group, operatorId)

        return post
    }

    @Transactional(readOnly = true)
    override fun getPosts(
        operatorId: UUID,
        groupId: UUID,
        pageable: Pageable
    ): Slice<Post> {
        val group = groupService.getGroupById(groupId)

        checkForMembership(group, operatorId)

        return repository.findAllByGroupOrderByCreatedAtDesc(group, pageable)
    }

    private fun checkForMembership(group: Group, operatorId: UUID) {
        if (group.isPrivate && !memberService.isMemberActive(
                group.id!!,
                operatorId,
                GroupMemberStatus.ACTIVE
            )) {
            throw IllegalArgumentException("You can't access this post due to private setting of the group")
        }
    }
}
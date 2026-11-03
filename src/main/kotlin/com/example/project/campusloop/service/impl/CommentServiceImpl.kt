package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.CreateCommentCommand
import com.example.project.campusloop.domain.command.EditCommentCommand
import com.example.project.campusloop.domain.entity.Comment
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import com.example.project.campusloop.repository.CommentRepository
import com.example.project.campusloop.service.CommentService
import com.example.project.campusloop.service.GroupMemberService
import com.example.project.campusloop.service.PostService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CommentServiceImpl(
    private val repository: CommentRepository,
    private val userService: UserService,
    private val memberService: GroupMemberService,
    private val postService: PostService
): CommentService{

    @Transactional
    override fun createComment(
        authorId: UUID,
        groupId: UUID,
        postId: UUID,
        command: CreateCommentCommand
    ): Comment {
        val author = userService.getUserById(authorId) ?:
            throw IllegalArgumentException("User $authorId not found")

        val post = postService.getPost(authorId, postId)

        val comment = Comment(
            content = command.content,
            post = post,
            author = author
        )

        return repository.save(comment)
    }

    @Transactional
    override fun editComment(
        operatorId: UUID,
        commentId: UUID,
        command: EditCommentCommand
    ): Comment {
        val comment = getComment(operatorId, commentId)

        if (comment.author.id!! != operatorId) {
            throw IllegalArgumentException("Only the comment authors can edit this comment")
        }

        comment.content = command.content
        return repository.save(comment)
    }

    @Transactional
    override fun deleteComment(operatorId: UUID, postId: UUID, commentId: UUID) {
        val comment = getComment(operatorId, commentId)

        if (comment.author.id!! != operatorId) {
            memberService.validateMemberRole(
                userId = operatorId,
                group = comment.post.group,
                requiredRole = GroupMemberRole.MODERATOR
            )
        }

        repository.delete(comment)
    }

    @Transactional(readOnly = true)
    override fun getComment(
        operatorId: UUID,
        commentId: UUID
    ): Comment {
        val comment = repository.findByIdOrNull(commentId)
            ?: throw IllegalArgumentException("Comment $commentId not found")

        val group = comment.post.group
        if (group.isPrivate && !memberService.isMemberActive(
                groupId = group.id!!,
                userId = operatorId,
                status = GroupMemberStatus.ACTIVE
            )) {
            throw IllegalArgumentException("You don't have access to this comment")
        }

        return comment
    }

    @Transactional(readOnly = true)
    override fun getCommentsByPost(
        operatorId: UUID,
        postId: UUID,
        pageable: Pageable
    ): Slice<Comment> {
        val post = postService.getPost(operatorId, postId)

        return repository.findAllByPostOrderByCreatedAtAsc(post, pageable)
    }

}
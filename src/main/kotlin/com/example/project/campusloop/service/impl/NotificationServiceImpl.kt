package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.dto.toResponse
import com.example.project.campusloop.domain.entity.Notification
import com.example.project.campusloop.domain.enum.NotificationType
import com.example.project.campusloop.repository.NotificationRepository
import com.example.project.campusloop.service.NotificationDispatchService
import com.example.project.campusloop.service.NotificationService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NotificationServiceImpl (
    private val repository: NotificationRepository,
    private val dispatchService: NotificationDispatchService,
    private val  userService: UserService
) : NotificationService {

    @Transactional
    override fun createAndSend(
        receiverId: UUID,
        title: String,
        message: String,
        type: NotificationType
    ): Notification {
        if (!userService.existById(receiverId)) {
            throw IllegalArgumentException("User not exists for id: $receiverId")
        }

        val notification = Notification(
            receiverId = receiverId,
            title = title,
            message = message,
            type = type
        )

        val saved = repository.save(notification)
        dispatchService.sendToUserDirectly(receiverId, saved.toResponse())

        return saved
    }

    @Transactional(readOnly = true)
    override fun getNotification(notificationId: UUID): Notification {
        return repository.findByIdOrNull(notificationId) ?:
            throw IllegalArgumentException("Notification not found for notificationId: $notificationId")
    }

    @Transactional(readOnly = true)
    override fun getNotifications(
        userId: UUID,
        pageable: Pageable
    ): Slice<Notification> {
        return repository.findAllByReceiverIdOrderByCreatedAtDesc(userId, pageable)
    }

    @Transactional
    override fun markAsRead(notificationId: UUID, receiverId: UUID) {
        val updatedRows = repository.markAsReadDirectly(notificationId, receiverId)
        if (updatedRows == 0) {
            throw IllegalArgumentException("Notification not found for receiverId: $receiverId")
        }
    }

    @Transactional
    override fun markAllAsRead(receiverId: UUID) {
        repository.markAllAsRead(receiverId)
    }

    @Transactional(readOnly = true)
    override fun getUnreadCount(receiverId: UUID): Long {
        return repository.countByReceiverIdAndIsReadFalse(receiverId)
    }

    @Transactional
    override fun deleteMyNotification(userId: UUID, notificationId: UUID) {
        val deletedRows = repository.deleteDirectly(notificationId, userId)
        if (deletedRows == 0) {
            throw IllegalArgumentException("Notification not found or access denied for id: $notificationId")
        }
    }
}
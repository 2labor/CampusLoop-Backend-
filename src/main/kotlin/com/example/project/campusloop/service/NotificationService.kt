package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Notification
import com.example.project.campusloop.domain.enum.NotificationType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface NotificationService {
    fun createAndSend(receiverId: UUID, title: String, message: String, type: NotificationType): Notification
    fun getNotification(notificationId: UUID): Notification
    fun getNotifications(userId: UUID, pageable: Pageable): Slice<Notification>
    fun markAsRead(notificationId: UUID, receiverId: UUID)
    fun markAllAsRead(receiverId: UUID)
    fun getUnreadCount(receiverId: UUID): Long
    fun deleteMyNotification(userId: UUID, notificationId: UUID)
}
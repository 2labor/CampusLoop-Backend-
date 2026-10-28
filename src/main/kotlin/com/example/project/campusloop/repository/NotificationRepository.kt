package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Notification
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findAllByReceiverIdOrderByCreatedAtDesc(receiverId: UUID, pageable: Pageable): Slice<Notification>

    fun countByReceiverIdAndIsReadFalse(receiverId: UUID): Long

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiverId = :receiverId AND n.isRead = false")
    fun markAllAsRead(receiverId: UUID): Int

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.receiverId = :receiverId")
    fun markAsReadDirectly(notificationId: UUID, receiverId: UUID): Int

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.id = :id AND n.receiverId = :receiverId")
    fun deleteDirectly(id: UUID, receiverId: UUID): Int
}
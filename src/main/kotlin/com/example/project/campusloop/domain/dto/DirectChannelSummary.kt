package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.User

data class DirectChannelSummary(
    val channel: Channel,
    val counterpart: User,
    val unreadCount: Int
)

package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.enum.ChannelType
import java.util.UUID

data class CreateChannelRequest(
    val groupId: UUID?,
    val name: String?,
    val type: ChannelType,
)

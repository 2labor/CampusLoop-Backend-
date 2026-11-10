package com.example.project.campusloop.domain.command

import com.example.project.campusloop.domain.enum.StoriesPrivacy

data class CreateStoryCommand(
    val mediaFileKey: String,
    val latitude: Double,
    val longitude: Double,
    val caption: String? = null,
    val privacy: StoriesPrivacy = StoriesPrivacy.EVERYONE
)

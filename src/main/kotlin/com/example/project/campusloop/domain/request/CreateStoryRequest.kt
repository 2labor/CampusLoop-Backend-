package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.enum.StoriesPrivacy

data class CreateStoryRequest(
    val mediaFileKey: String,
    val latitude: Double,
    val longitude: Double,
    val caption: String? = null,
    val privacy: StoriesPrivacy = StoriesPrivacy.EVERYONE
)

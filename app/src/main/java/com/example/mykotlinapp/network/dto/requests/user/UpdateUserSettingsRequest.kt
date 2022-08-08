package com.example.mykotlinapp.network.dto.requests.user

import com.example.mykotlinapp.domain.pojo.Language

data class UpdateUserSettingsRequest(
    val language: Language,
    val eventPushNotifications: Boolean,
    val chatPushNotifications: Boolean,
)

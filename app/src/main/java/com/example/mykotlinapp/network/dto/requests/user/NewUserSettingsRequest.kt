package com.example.mykotlinapp.network.dto.requests.user

import com.example.mykotlinapp.domain.pojo.Language

data class NewUserSettingsRequest(
    val language: Language,
)
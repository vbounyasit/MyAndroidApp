package com.example.mykotlinapp.network.dto.requests.user

import com.example.mykotlinapp.domain.pojo.Gender

data class SignUserUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val birthDay: String,
    val gender: Gender,
    val settings: NewUserSettingsRequest,
)

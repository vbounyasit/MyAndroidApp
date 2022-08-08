package com.example.mykotlinapp.network.dto.requests.user

data class LogInUserRequest(
    val id: String,
    val password: String,
    val deviceId: String,
)

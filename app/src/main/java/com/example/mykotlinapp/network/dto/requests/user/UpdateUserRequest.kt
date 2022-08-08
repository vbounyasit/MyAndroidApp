package com.example.mykotlinapp.network.dto.requests.user

data class UpdateUserRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val profilePicture: String,
    val description: String?,
)
package com.example.mykotlinapp.network.dto.requests.chat

data class CreateChatRequest(
    val name: String?,
    val profilePicture: String?,
    val firstChatLog: String,
    val participants: List<String>,
)

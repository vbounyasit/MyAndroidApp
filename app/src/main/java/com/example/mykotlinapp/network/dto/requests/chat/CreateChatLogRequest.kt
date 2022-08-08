package com.example.mykotlinapp.network.dto.requests.chat

data class CreateChatLogRequest(
    val chatRemoteId: String,
    val content: String,
)
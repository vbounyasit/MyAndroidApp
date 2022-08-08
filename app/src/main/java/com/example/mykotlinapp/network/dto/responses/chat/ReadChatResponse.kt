package com.example.mykotlinapp.network.dto.responses.chat

data class ReadChatResponse(
    val chatRemoteId: String,
    val lastReadTime: Long,
)

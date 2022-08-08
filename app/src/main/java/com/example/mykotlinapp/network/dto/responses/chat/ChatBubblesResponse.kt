package com.example.mykotlinapp.network.dto.responses.chat

data class ChatBubblesResponse(
    val logs: List<ChatLogResponse>,
    val notifications: List<ChatNotificationResponse>,
)
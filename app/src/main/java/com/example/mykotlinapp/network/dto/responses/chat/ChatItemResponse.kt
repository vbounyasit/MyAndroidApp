package com.example.mykotlinapp.network.dto.responses.chat

import com.example.mykotlinapp.network.dto.responses.chat.ChatResponses.ChatDataResponse

data class ChatItemResponse(
    val chat: ChatDataResponse,
    val lastActive: Long,
    val lastReadTime: Long?,
    val latestChatLog: ChatLogResponse,
    val isGroupChat: Boolean,
)
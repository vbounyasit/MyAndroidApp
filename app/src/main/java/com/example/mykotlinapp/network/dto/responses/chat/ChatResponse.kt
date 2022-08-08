package com.example.mykotlinapp.network.dto.responses.chat

import com.example.mykotlinapp.network.dto.responses.chat.ChatResponses.ChatDataResponse
import com.example.mykotlinapp.network.dto.responses.chat.ChatResponses.ChatParticipantsResponse
import com.example.mykotlinapp.network.dto.responses.chat.ChatResponses.GroupDataResponse

data class ChatResponse(
    val chat: ChatDataResponse,
    val group: GroupDataResponse,
    val lastActive: Long,
    val lastReadTime: Long?,
    val lastGroupReadTime: Long?,
    val isAdmin: Boolean,
    val participantsData: ChatParticipantsResponse,
)
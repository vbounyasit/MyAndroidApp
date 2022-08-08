package com.example.mykotlinapp.network.dto.responses.chat

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse

data class ChatLogResponse(
    val remoteId: String,
    val chatRemoteId: String,
    val author: UserContactResponse,
    val content: String,
    val creationDate: Long,
    val isMe: Boolean,
)
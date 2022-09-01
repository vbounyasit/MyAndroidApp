package com.example.mykotlinapp.network.dto.responses.chat

data class ChatNotificationResponse(
    val remoteId: String,
    val chatRemoteId: String,
    val content: String,
    val creationTimeStamp: Long,
    val updateTimeStamp: Long
)
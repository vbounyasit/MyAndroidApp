package com.example.mykotlinapp.model.dto.ui.chat

data class ChatNotificationDTO(
    override val remoteId: String,
    val content: String,
    override val creationTimeStamp: Long,
) : ChatBubbleDTO
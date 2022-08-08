package com.example.mykotlinapp.model.dto.ui.chat

import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO

data class ChatLogDTO(
    override val remoteId: String,
    val author: UserContactDTO?,
    val content: String,
    val creationDate: String,
    override val creationTimeStamp: Long,
    val isMe: Boolean,
    val upToDate: Boolean,
    val readBy: List<ChatParticipantDTO> = listOf(),
) : ChatBubbleDTO
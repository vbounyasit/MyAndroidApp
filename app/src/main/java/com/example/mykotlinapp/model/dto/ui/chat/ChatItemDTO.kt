package com.example.mykotlinapp.model.dto.ui.chat

import com.example.mykotlinapp.domain.pojo.ActivityStatus

data class ChatItemDTO(
    val remoteId: String,
    val name: String,
    val profilePictures: List<String>,
    val lastActive: ActivityStatus,
    val lastRead: Long?,
    val isGroupChat: Boolean,
)
package com.example.mykotlinapp.model.dto.ui.chat

data class ChatDTO(
    val remoteId: String,
    val groupRemoteId: String,
    val name: String,
    val profilePictures: List<String>,
    val subTitle: String?,
    val isAdmin: Boolean,
)
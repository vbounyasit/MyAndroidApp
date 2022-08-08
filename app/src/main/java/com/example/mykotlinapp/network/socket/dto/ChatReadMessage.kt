package com.example.mykotlinapp.network.socket.dto

data class ChatReadMessage(
    val chatRemoteId: String,
    val participantRemoteId: String,
    val readTime: Long,
)
package com.example.mykotlinapp.network.dto.responses.chat

data class ReadGroupResponse(
    val groupRemoteId: String,
    val lastGroupReadTime: Long,
)
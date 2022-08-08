package com.example.mykotlinapp.network.dto.requests.chat

data class UpdateGroupRequest(
    val groupRemoteId: String,
    val name: String?,
    val description: String?,
)

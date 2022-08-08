package com.example.mykotlinapp.network.dto.requests.comment

data class CreateCommentRequest(
    val content: String,
    val parentRemoteId: String?,
)
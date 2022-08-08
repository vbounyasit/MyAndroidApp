package com.example.mykotlinapp.network.dto.requests.comment

data class UpdateCommentRequest(
    val commentRemoteId: String,
    val groupRemoteId: String,
    val content: String,
)

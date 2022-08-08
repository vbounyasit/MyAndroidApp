package com.example.mykotlinapp.network.dto.requests.comment

data class DeleteCommentRequest(
    val commentRemoteId: String,
    val groupRemoteId: String,
)
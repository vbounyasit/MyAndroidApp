package com.example.mykotlinapp.network.dto.requests.post

data class UpdatePostRequest(
    val postRemoteId: String,
    val groupRemoteId: String,
    val content: String,
)
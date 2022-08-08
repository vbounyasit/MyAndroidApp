package com.example.mykotlinapp.network.dto.requests.post

data class DeletePostRequest(
    val postRemoteId: String,
    val groupRemoteId: String,
)
package com.example.mykotlinapp.network.dto.requests.post

data class CreatePostRequest(
    val content: String,
    val medias: List<String>?,
)

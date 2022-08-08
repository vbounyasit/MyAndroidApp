package com.example.mykotlinapp.network.dto.requests.post

data class UpdatePostVoteRequest(
    val postRemoteId: String,
    val groupRemoteId: String,
    val voteState: Int,
)

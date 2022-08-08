package com.example.mykotlinapp.network.dto.requests.comment

data class UpdateCommentVoteRequest(
    val commentRemoteId: String,
    val groupRemoteId: String,
    val voteState: Int,
)

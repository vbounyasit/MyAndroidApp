package com.example.mykotlinapp.network.dto.responses.post

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse

data class CommentResponse(
    val remoteId: String,
    val postRemoteId: String,
    val groupRemoteId: String,
    val content: String,
    val voteCount: Int,
    val depthLevel: Int,
    val isLast: Boolean,
    val creationDate: Long,
    val editDate: Long?,
    val parentRemoteId: String?,
    val voteState: Int,
    val creator: UserContactResponse,
    val isCreator: Boolean,
)
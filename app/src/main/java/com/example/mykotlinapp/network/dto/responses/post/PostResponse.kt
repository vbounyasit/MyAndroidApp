package com.example.mykotlinapp.network.dto.responses.post

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse

data class PostResponse(
    val remoteId: String,
    val groupRemoteId: String,
    val content: String,
    val medias: List<PostMediaResponse>,
    val votesCount: Int,
    val commentsCount: Int,
    val voteState: Int,
    val creator: UserContactResponse,
    val isCreator: Boolean,
    val creationTimeStamp: Long,
    val updateTimeStamp: Long
)
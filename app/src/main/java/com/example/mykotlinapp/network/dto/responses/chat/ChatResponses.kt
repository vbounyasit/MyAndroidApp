package com.example.mykotlinapp.network.dto.responses.chat

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse

object ChatResponses {

    data class ChatDataResponse(
        val remoteId: String,
        val name: String,
        val profilePicture: String,
    )

    data class GroupDataResponse(
        val remoteId: String,
        val description: String?,
        val backgroundPicture: String?,
    )

    data class ChatParticipantsResponse(
        val displayedParticipants: List<ChatParticipantResponse>,
        val remainingCount: Int,
    )

    data class ChatParticipantResponse(
        val data: ParticipantDataResponse,
        val user: UserContactResponse,
    )

    data class ParticipantDataResponse(
        val isAdmin: Boolean,
        val lastReadTime: Long?,
    )

}
package com.example.mykotlinapp.model.dto.ui.chat

import com.example.mykotlinapp.model.dto.ui.Participant

data class ChatParticipantDTO(
    override val remoteId: String,
    val name: String,
    override val profilePicture: String,
    val lastReadTime: Long?,
) : Participant

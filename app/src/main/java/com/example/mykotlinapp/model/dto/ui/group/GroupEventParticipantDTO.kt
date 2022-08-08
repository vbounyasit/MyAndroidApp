package com.example.mykotlinapp.model.dto.ui.group

import com.example.mykotlinapp.model.dto.ui.Participant

data class GroupEventParticipantDTO(override val remoteId: String, override val profilePicture: String) : Participant
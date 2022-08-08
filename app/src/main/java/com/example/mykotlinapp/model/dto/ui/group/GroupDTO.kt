package com.example.mykotlinapp.model.dto.ui.group

data class GroupDTO(
    val remoteId: String,
    val chatRemoteId: String,
    val name: String,
    val description: String?,
    val profilePictures: List<String>,
    val backgroundPicture: String?,
    val extraParticipantCount: Int,
    val isAdmin: Boolean,
)
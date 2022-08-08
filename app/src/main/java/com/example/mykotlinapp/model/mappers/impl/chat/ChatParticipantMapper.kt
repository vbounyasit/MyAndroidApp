package com.example.mykotlinapp.model.mappers.impl.chat

import com.example.mykotlinapp.domain.pojo.ActivityStatus
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.model.dto.ui.chat.ChatParticipantDTO
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.entity.chat.ChatParticipant
import com.example.mykotlinapp.model.mappers.DTOMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.network.dto.responses.chat.ChatResponse

object ChatParticipantMapper : DTOMapper<ChatParticipant, ChatParticipantDTO>, NetworkResponseMapper<ChatResponse, List<ChatParticipant>> {
    override fun toDTO(entity: ChatParticipant): ChatParticipantDTO =
        ChatParticipantDTO(
            entity.chatRemoteId,
            entity.fullName,
            entity.profilePicture,
            entity.lastReadTime
        )

    override fun toEntity(networkData: ChatResponse): List<ChatParticipant> {
        return networkData.participantsData.displayedParticipants.map {
            ChatParticipant(
                it.user.remoteId,
                networkData.chat.remoteId,
                it.user.firstName,
                it.user.lastName,
                it.user.fullName,
                it.user.profilePicture,
                it.data.isAdmin,
                it.data.lastReadTime
            )
        }
    }

    object ChatContactParticipantMapper : DTOMapper<ChatParticipant, UserContactDTO> {
        override fun toDTO(entity: ChatParticipant): UserContactDTO {
            return UserContactDTO(
                entity.remoteId,
                entity.firstName,
                entity.lastName,
                entity.fullName,
                entity.profilePicture,
                null,
                ActivityStatus.ONLINE,
                ContactRelationType.NONE
            )
        }

    }
}
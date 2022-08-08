package com.example.mykotlinapp.model.mappers.impl.chat

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.chat.ChatNotificationDTO
import com.example.mykotlinapp.model.entity.chat.ChatNotification
import com.example.mykotlinapp.model.mappers.DTOMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.network.dto.responses.chat.ChatNotificationResponse

object ChatNotificationMapper : DTOMapper<ChatNotification, ChatNotificationDTO>, NetworkResponseMapper<ChatNotificationResponse, ChatNotification> {
    override fun toDTO(entity: ChatNotification): ChatNotificationDTO {
        return ChatNotificationDTO(
            entity.remoteId,
            entity.content,
            entity.creationDate
        )
    }

    override fun toEntity(networkData: ChatNotificationResponse): ChatNotification {
        return ChatNotification(
            networkData.remoteId,
            networkData.chatRemoteId,
            networkData.content,
            networkData.creationDate,
            SyncState.UP_TO_DATE
        )
    }
}
package com.example.mykotlinapp.model.mappers.impl.chat

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.chat.ChatDTO
import com.example.mykotlinapp.model.entity.chat.ChatProperty
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.toFormattedTimeAgo
import com.example.mykotlinapp.network.dto.responses.chat.ChatResponse

object ChatPropertyMapper :
    DTOMapperWithParam<ChatProperty, ChatDTO, Context>,
    NetworkResponseMapper<ChatResponse, ChatProperty> {

    override fun toEntity(networkData: ChatResponse): ChatProperty {
        return ChatProperty(
            networkData.chat.remoteId,
            networkData.group.remoteId,
            networkData.chat.name,
            networkData.chat.profilePicture,
            networkData.lastActive,
            networkData.lastReadTime,
            networkData.isAdmin,
            SyncState.UP_TO_DATE,
            networkData.creationTimeStamp,
            networkData.updateTimeStamp
        )
    }

    override fun toDTO(parameter: Context): (entity: ChatProperty) -> ChatDTO =
        { entity ->
            val lastActive = toFormattedTimeAgo(parameter, entity.lastActive, suffix = " ago").lowercase()
            val lastActiveTitle = if (lastActive.isNotBlank()) "Active $lastActive" else null
            ChatDTO(
                entity.remoteId,
                entity.groupRemoteId,
                entity.name,
                entity.profilePicture.split(parameter.getString(R.string.profile_pictures_delimiter)),
                lastActiveTitle,
                entity.isAdmin
            )
        }

}
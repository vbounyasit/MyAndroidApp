package com.example.mykotlinapp.model.mappers.impl.chat

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.chat.ChatDTO
import com.example.mykotlinapp.model.entity.chat.ChatProperty
import com.example.mykotlinapp.model.mappers.DTOContextMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.toTimeAgo
import com.example.mykotlinapp.network.dto.responses.chat.ChatResponse

object ChatPropertyMapper :
    DTOContextMapper<ChatProperty, ChatDTO>,
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

    override fun toDTO(context: Context): (entity: ChatProperty) -> ChatDTO =
        { entity ->
            val lastActive = toTimeAgo(context, entity.lastActive, suffix = " ago").lowercase()
            val lastActiveTitle = if (lastActive.isNotBlank()) "Active $lastActive" else null
            ChatDTO(
                entity.remoteId,
                entity.groupRemoteId,
                entity.name,
                entity.profilePicture.split(context.getString(R.string.profile_pictures_delimiter)),
                lastActiveTitle,
                entity.isAdmin
            )
        }

}
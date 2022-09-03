package com.example.mykotlinapp.model.mappers.impl.chat

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.chat.ChatItemDTO
import com.example.mykotlinapp.model.entity.chat.ChatItem
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils
import com.example.mykotlinapp.network.dto.responses.chat.ChatItemResponse

object ChatItemMapper : DTOMapperWithParam<ChatItem, ChatItemDTO, Context>,
    NetworkResponseMapper<ChatItemResponse, ChatItem> {

    override fun toEntity(networkData: ChatItemResponse): ChatItem {
        return ChatItem(
            networkData.chat.remoteId,
            networkData.chat.name,
            networkData.chat.profilePicture,
            networkData.lastActive,
            networkData.lastReadTime,
            networkData.isGroupChat,
            SyncState.UP_TO_DATE,
            networkData.creationTimeStamp,
            networkData.updateTimeStamp
        )
    }

    override fun toDTO(parameter: Context): (entity: ChatItem) -> ChatItemDTO =
        { entity ->
            val pictures = entity.profilePicture.split(parameter.getString(R.string.profile_pictures_delimiter))
            val lastActive = Utils.toActivityStatus(parameter, System.currentTimeMillis(), entity.lastActive)
            ChatItemDTO(
                entity.remoteId,
                entity.name,
                pictures,
                lastActive,
                entity.lastReadTime,
                entity.isGroupChat
            )
        }
}
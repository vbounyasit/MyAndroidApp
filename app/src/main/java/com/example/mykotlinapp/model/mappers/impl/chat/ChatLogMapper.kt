package com.example.mykotlinapp.model.mappers.impl.chat

import android.content.Context
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.chat.ChatLogDTO
import com.example.mykotlinapp.model.entity.chat.ChatLog
import com.example.mykotlinapp.model.mappers.DTOContextMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.toChatLogTime
import com.example.mykotlinapp.model.mappers.impl.user.UserContactMapper
import com.example.mykotlinapp.network.dto.responses.chat.ChatLogResponse

object ChatLogMapper :
    DTOContextMapper<ChatLog, ChatLogDTO>,
    NetworkResponseMapper<ChatLogResponse, ChatLog> {

    override fun toEntity(networkData: ChatLogResponse): ChatLog {
        return ChatLog(
            networkData.remoteId,
            networkData.chatRemoteId,
            UserContactMapper.toEntity(networkData.author, ContactRelationType.NONE),
            networkData.content,
            networkData.isMe,
            SyncState.UP_TO_DATE,
            networkData.creationTimeStamp,
            networkData.updateTimeStamp
        )
    }

    override fun toDTO(context: Context): (entity: ChatLog) -> ChatLogDTO =
        { entity ->
            ChatLogDTO(
                entity.remoteId,
                UserContactMapper.toDTO(context)(entity.author),
                entity.content,
                toChatLogTime(context, entity.creationTime),
                entity.creationTime,
                entity.isMe,
                upToDate = true
            )
        }
}
package com.example.mykotlinapp.model.mappers.impl.chat.create

import android.content.Context
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatLogInput
import com.example.mykotlinapp.model.dto.ui.chat.ChatLogDTO
import com.example.mykotlinapp.model.entity.pending.PendingChatLogCreation
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.InputCreateMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.toChatLogTime

object PendingChatLogMapper : InputCreateMapper<CreateChatLogInput, PendingChatLogCreation>,
    DTOMapperWithParam<PendingChatLogCreation, ChatLogDTO, Context> {

    override fun toEntity(input: CreateChatLogInput): PendingChatLogCreation {
        return PendingChatLogCreation(
            chatRemoteId = input.chatRemoteId,
            content = input.content,
            creationDate = System.currentTimeMillis()
        )
    }

    override fun toDTO(parameter: Context): (entity: PendingChatLogCreation) -> ChatLogDTO =
        { entity ->
            ChatLogDTO(
                remoteId = entity.id.toString(),
                author = null,
                content = entity.content,
                creationDate = toChatLogTime(parameter, entity.creationDate),
                creationTimeStamp = entity.creationDate,
                isMe = true,
                upToDate = false
            )
        }
}
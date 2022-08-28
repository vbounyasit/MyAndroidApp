package com.example.mykotlinapp.model.mappers.impl.chat.create

import android.content.Context
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatLogInput
import com.example.mykotlinapp.model.dto.ui.chat.ChatLogDTO
import com.example.mykotlinapp.model.entity.pending.PendingChatLogCreation
import com.example.mykotlinapp.model.mappers.DTOContextMapper
import com.example.mykotlinapp.model.mappers.InputCreateMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.toChatLogTime

object PendingChatLogMapper : InputCreateMapper<CreateChatLogInput, PendingChatLogCreation>,
    DTOContextMapper<PendingChatLogCreation, ChatLogDTO> {

    override fun toEntity(input: CreateChatLogInput): PendingChatLogCreation {
        return PendingChatLogCreation(
            chatRemoteId = input.chatRemoteId,
            content = input.content,
            creationDate = System.currentTimeMillis()
        )
    }

    override fun toDTO(context: Context): (entity: PendingChatLogCreation) -> ChatLogDTO =
        { entity ->
            ChatLogDTO(
                remoteId = entity.id.toString(),
                author = null,
                content = entity.content,
                creationDate = toChatLogTime(context, entity.creationDate),
                creationTimeStamp = entity.creationDate,
                isMe = true,
                upToDate = false
            )
        }
}
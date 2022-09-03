package com.example.mykotlinapp.model.mappers.impl.chat

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.model.dto.ui.chat.ChatListItemDTO
import com.example.mykotlinapp.model.entity.chat.ChatItem
import com.example.mykotlinapp.model.entity.chat.ChatLog
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.impl.Utils

object ChatListItemMapper : DTOMapperWithParam<Pair<ChatItem, ChatLog>, ChatListItemDTO, Context> {

    override fun toDTO(parameter: Context): (entity: Pair<ChatItem, ChatLog>) -> ChatListItemDTO =
        { entity ->
            val (chatItem, chatLog) = entity
            val delimiter = parameter.getString(R.string.chat_log_author_delimiter)
            val prefix: String = if (chatLog.isMe) "${parameter.getString(R.string.chat_item_author_me)}$delimiter" else "${chatLog.author.firstName}$delimiter"
            val lastChatLog = "$prefix${chatLog.content}"
            val lastChatLogCreationDate: String = Utils.toFormattedTimeAgo(parameter, chatLog.creationTime)
            val read = chatItem.lastReadTime?.let { chatLog.creationTime <= it || chatLog.isMe } ?: false
            ChatListItemDTO(
                ChatItemMapper.toDTO(parameter)(chatItem),
                ChatLogMapper.toDTO(parameter)(chatLog).copy(creationDate = lastChatLogCreationDate, content = lastChatLog),
                read
            )
        }

}
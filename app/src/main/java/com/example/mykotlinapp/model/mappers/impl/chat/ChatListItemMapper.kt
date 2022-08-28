package com.example.mykotlinapp.model.mappers.impl.chat

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.model.dto.ui.chat.ChatListItemDTO
import com.example.mykotlinapp.model.entity.chat.ChatItem
import com.example.mykotlinapp.model.entity.chat.ChatLog
import com.example.mykotlinapp.model.mappers.DTOContextMapper
import com.example.mykotlinapp.model.mappers.impl.Utils

object ChatListItemMapper : DTOContextMapper<Pair<ChatItem, ChatLog>, ChatListItemDTO> {

    override fun toDTO(context: Context): (entity: Pair<ChatItem, ChatLog>) -> ChatListItemDTO =
        { entity ->
            val (chatItem, chatLog) = entity
            val delimiter = context.getString(R.string.chat_log_author_delimiter)
            val prefix: String =
                if (chatLog.isMe)
                    "${context.getString(R.string.chat_item_author_me)}$delimiter"
                else
                    "${chatLog.author.firstName}$delimiter"
            val lastChatLog = "$prefix${chatLog.content}"
            val lastChatLogCreationDate: String = Utils.toTimeAgo(context, chatLog.creationDate)
            val read =
                chatItem.lastReadTime?.let { chatLog.creationDate <= it || chatLog.isMe } ?: false
            ChatListItemDTO(
                ChatItemMapper.toDTO(context)(chatItem),
                ChatLogMapper.toDTO(context)(chatLog)
                    .copy(creationDate = lastChatLogCreationDate, content = lastChatLog),
                read
            )
        }

}
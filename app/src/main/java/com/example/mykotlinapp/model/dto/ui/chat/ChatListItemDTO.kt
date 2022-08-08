package com.example.mykotlinapp.model.dto.ui.chat

import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

data class ChatListItemDTO(
    val chatItem: ChatItemDTO,
    val lastChatLog: ChatLogDTO,
    val read: Boolean,
) : RecyclerViewItem {
    override val remoteId: String = chatItem.remoteId
}
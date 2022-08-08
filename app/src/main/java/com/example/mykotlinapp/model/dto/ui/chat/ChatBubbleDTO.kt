package com.example.mykotlinapp.model.dto.ui.chat

import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

interface ChatBubbleDTO : RecyclerViewItem {
    val creationTimeStamp: Long
}
package com.example.mykotlinapp.ui.screens.chats.history

import android.graphics.Typeface
import com.example.mykotlinapp.databinding.ItemChatBinding
import com.example.mykotlinapp.model.dto.ui.chat.ChatListItemDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener

class ChatListAdapter(listener: ClickListener<ChatListItemDTO>) :
    AppListAdapter<ChatListItemDTO, ItemChatBinding>(
        bindData = { property, binding ->
            binding.property = property
            binding.listener = listener
            if (!property.read) binding.chatLatestMessage.setTypeface(null, Typeface.BOLD_ITALIC)
            binding.chatActivityStatus.setImageResource(property.chatItem.lastActive.icon)
        },
        inflate = { it, _ -> ItemChatBinding.inflate(it) },
    )
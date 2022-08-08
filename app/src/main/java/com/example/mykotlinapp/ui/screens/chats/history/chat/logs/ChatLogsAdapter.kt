package com.example.mykotlinapp.ui.screens.chats.history.chat.logs

import android.widget.RelativeLayout
import androidx.databinding.ViewDataBinding
import com.example.mykotlinapp.databinding.ItemChatLogBinding
import com.example.mykotlinapp.databinding.ItemChatNotificationBinding
import com.example.mykotlinapp.model.dto.ui.chat.ChatBubbleDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatLogDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatNotificationDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.screens.chats.ChatParticipantAdapter

class ChatLogsAdapter(private val isChatGroup: Boolean, readParticipantSize: Int) :
    AppListAdapter<ChatBubbleDTO, ViewDataBinding>(
        bindData = { property, binding ->
            if (property is ChatLogDTO && binding is ItemChatLogBinding) {
                binding.property = property
                binding.chatIsGroup = isChatGroup
                val layoutParam = binding.chatBubbleCardView.layoutParams as RelativeLayout.LayoutParams
                if (property.isMe)
                    layoutParam.addRule(RelativeLayout.ALIGN_PARENT_END)
                else
                    layoutParam.removeRule(RelativeLayout.ALIGN_PARENT_END)
                //read participants
                binding.readParticipants.adapter = ChatParticipantAdapter(readParticipantSize)
            } else if (property is ChatNotificationDTO && binding is ItemChatNotificationBinding)
                binding.notification = property.content
        },
        inflate = { it, viewType ->
            when (viewType) {
                ChatBubbleType.CHAT_LOG.ordinal -> ItemChatLogBinding.inflate(it)
                else -> ItemChatNotificationBinding.inflate(it)
            }
        }
    ) {
    private enum class ChatBubbleType {
        CHAT_LOG, CHAT_NOTIFICATION
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatLogDTO -> ChatBubbleType.CHAT_LOG.ordinal
            else -> ChatBubbleType.CHAT_NOTIFICATION.ordinal
        }
    }
}
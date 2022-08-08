package com.example.mykotlinapp.ui.screens.chats.history.group.events

import com.example.mykotlinapp.databinding.ItemEventBinding
import com.example.mykotlinapp.model.dto.ui.group.GroupEventItemDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import com.example.mykotlinapp.ui.screens.chats.ChatParticipantAdapter

class GroupEventsAdapter(listener: ClickListener<GroupEventItemDTO>, participantPicSize: Int) :
    AppListAdapter<GroupEventItemDTO, ItemEventBinding>(
        bindData = { property, binding ->
            binding.property = property
            binding.eventParticipationIcon.setImageResource(property.joinState.icon)
            binding.eventParticipantList.adapter = ChatParticipantAdapter(participantPicSize)
            binding.listener = listener
        },
        inflate = { inflater, _ -> ItemEventBinding.inflate(inflater) }
    )
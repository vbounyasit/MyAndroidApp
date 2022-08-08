package com.example.mykotlinapp.ui.screens.chats

import com.example.mykotlinapp.databinding.ItemParticipantBinding
import com.example.mykotlinapp.model.dto.ui.Participant
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter

class ChatParticipantAdapter(pictureSize: Int) :
    AppListAdapter<Participant, ItemParticipantBinding>(
        bindData = { property, binding ->
            binding.property = property
            binding.pictureSize = pictureSize
        },
        inflate = { inflater, _ -> ItemParticipantBinding.inflate(inflater) }
    )
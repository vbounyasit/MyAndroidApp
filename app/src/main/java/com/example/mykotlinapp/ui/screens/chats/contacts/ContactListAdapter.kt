package com.example.mykotlinapp.ui.screens.chats.contacts

import com.example.mykotlinapp.databinding.ItemContactBinding
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener

class ContactListAdapter(
    listener: ClickListener<UserContactDTO>,
    acceptListener: ClickListener<UserContactDTO>,
    declineListener: ClickListener<UserContactDTO>,
) : AppListAdapter<UserContactDTO, ItemContactBinding>(
    bindData = { property, binding ->
        binding.property = property
        binding.listener = listener
        binding.acceptListener = acceptListener
        binding.declineListener = declineListener
    },
    inflate = { it, _ -> ItemContactBinding.inflate(it) })
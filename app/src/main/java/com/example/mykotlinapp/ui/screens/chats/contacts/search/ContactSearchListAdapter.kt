package com.example.mykotlinapp.ui.screens.chats.contacts.search

import com.example.mykotlinapp.databinding.ItemContactSearchBinding
import com.example.mykotlinapp.domain.pojo.PayloadType
import com.example.mykotlinapp.model.dto.inputs.ui_item.impl.UpdateContactInputUI
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener

class ContactSearchListAdapter(
    listener: ClickListener<UserContactDTO>,
    sendRequestListener: ClickListener<UserContactDTO>,
) : AppListAdapter<UserContactDTO, ItemContactSearchBinding>(
    bindData = { property, binding ->
        binding.property = property
        binding.listener = listener
        binding.sendRequestListener = sendRequestListener
    },
    inflate = { it, _ -> ItemContactSearchBinding.inflate(it) },
    payloadsMetadata = listOf(
        Companion.PayloadMetaData(
            payloadType = PayloadType.PAYLOAD_CONTACT_REQUEST_TYPE,
            toInputDTO = { UpdateContactInputUI(it.remoteId, it.relationType) },
            applyChangesFromPayload = { inputDTO, binding ->
                val relationType = (inputDTO as UpdateContactInputUI).relationType
                binding.property?.let { binding.property = it.copy(relationType = relationType) }
            }
        )
    )
)
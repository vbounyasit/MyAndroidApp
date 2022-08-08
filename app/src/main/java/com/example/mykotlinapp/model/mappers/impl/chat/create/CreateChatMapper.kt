package com.example.mykotlinapp.model.mappers.impl.chat.create

import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatInput
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.chat.CreateChatRequest

object CreateChatMapper : NetworkRequestMapper<CreateChatInput, CreateChatRequest> {
    override fun toNetworkRequest(entity: CreateChatInput): CreateChatRequest {
        return CreateChatRequest(
            entity.groupName,
            null,
            entity.firstChatLog,
            entity.contactRemoteIds
        )
    }
}
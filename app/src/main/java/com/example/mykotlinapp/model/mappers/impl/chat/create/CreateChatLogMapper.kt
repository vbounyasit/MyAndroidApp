package com.example.mykotlinapp.model.mappers.impl.chat.create

import com.example.mykotlinapp.model.entity.pending.PendingChatLogCreation
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.chat.CreateChatLogRequest

object CreateChatLogMapper : NetworkRequestMapper<PendingChatLogCreation, CreateChatLogRequest> {
    override fun toNetworkRequest(entity: PendingChatLogCreation): CreateChatLogRequest {
        return CreateChatLogRequest(entity.chatRemoteId, entity.content)
    }
}
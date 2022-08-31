package com.example.mykotlinapp.model.mappers.impl.chat.update

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateChatInput
import com.example.mykotlinapp.model.entity.chat.ChatProperty
import com.example.mykotlinapp.model.mappers.InputUpdateMapper

object UpdateChatMapper : InputUpdateMapper<UpdateChatInput, ChatProperty> {
    override fun toLocalUpdate(inputData: UpdateChatInput): (ChatProperty) -> ChatProperty {
        return { it.copy(name = inputData.name, syncState = SyncState.PENDING_UPDATE) }
    }
}
package com.example.mykotlinapp.model.dto.inputs.form.chat

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class CreateChatInput(
    val groupName: String?,
    val firstChatLog: String,
    val contactRemoteIds: List<String>,
) : InputDTO
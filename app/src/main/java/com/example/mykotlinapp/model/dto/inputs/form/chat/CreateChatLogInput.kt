package com.example.mykotlinapp.model.dto.inputs.form.chat

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class CreateChatLogInput(
    val chatRemoteId: String,
    val content: String,
) : InputDTO
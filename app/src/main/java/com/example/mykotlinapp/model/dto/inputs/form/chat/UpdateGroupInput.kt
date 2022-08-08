package com.example.mykotlinapp.model.dto.inputs.form.chat

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class UpdateGroupInput(
    val remoteId: String,
    val name: String?,
    val description: String?,
) : InputDTO
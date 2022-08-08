package com.example.mykotlinapp.model.dto.inputs.form.comment

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class UpdateCommentInput(
    val remoteId: String,
    val content: String,
) : InputDTO

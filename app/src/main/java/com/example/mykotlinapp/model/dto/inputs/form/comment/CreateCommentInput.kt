package com.example.mykotlinapp.model.dto.inputs.form.comment

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class CreateCommentInput(
    val parentRemoteId: String?,
    val content: String,
    val replyingTo: String,
) : InputDTO
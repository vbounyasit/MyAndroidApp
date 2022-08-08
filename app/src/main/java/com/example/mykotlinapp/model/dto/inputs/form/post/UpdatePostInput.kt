package com.example.mykotlinapp.model.dto.inputs.form.post

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class UpdatePostInput(
    val remoteId: String,
    val content: String,
) : InputDTO
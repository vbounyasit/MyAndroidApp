package com.example.mykotlinapp.model.dto.inputs.form.post

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class CreatePostInput(
    val content: String,
) : InputDTO
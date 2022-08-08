package com.example.mykotlinapp.model.dto.inputs.form.user

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class UpdateUserInput(
    val firstName: String,
    val lastName: String,
    val email: String,
    val aboutMe: String?,
) : InputDTO
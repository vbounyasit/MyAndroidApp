package com.example.mykotlinapp.model.dto.inputs.form.user

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class LogInUserInput(
    val id: String,
    val password: String,
    val rememberMe: Boolean,
) : InputDTO
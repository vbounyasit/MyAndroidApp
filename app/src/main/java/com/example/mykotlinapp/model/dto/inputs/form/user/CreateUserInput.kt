package com.example.mykotlinapp.model.dto.inputs.form.user

import com.example.mykotlinapp.domain.pojo.Gender
import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class CreateUserInput(
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val birthday: String,
    val gender: Gender,
) : InputDTO
package com.example.mykotlinapp.model.dto.ui.user

import com.example.mykotlinapp.domain.pojo.Gender

data class UserDTO(
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val profilePicture: String,
    val profileBackgroundPicture: String?,
    val description: String?,
    val gender: Gender,
    val age: Int,
)
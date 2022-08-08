package com.example.mykotlinapp.model.dto.ui.post

data class PostGroupData(
    val groupName: String,
    val groupPictures: List<String>,
    val isAdmin: Boolean,
)
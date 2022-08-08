package com.example.mykotlinapp.network.dto.responses.user.contact

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse

data class CreatedContactResponse(
    val contact: UserContactResponse,
    val accepted: Boolean,
)
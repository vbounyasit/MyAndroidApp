package com.example.mykotlinapp.network.dto.responses.user.contact

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse

data class UserContactListResponse(
    val sentRequests: List<UserContactResponse>,
    val receivedRequests: List<UserContactResponse>,
    val contacts: List<UserContactResponse>,
)

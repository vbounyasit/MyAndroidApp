package com.example.mykotlinapp.network.dto.responses.user.contact

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse

data class SearchContactResponse(
    val contacts: List<UserContactResponse>,
    val searchResults: List<UserContactResponse>,
)

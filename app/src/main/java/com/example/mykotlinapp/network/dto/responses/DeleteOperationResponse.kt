package com.example.mykotlinapp.network.dto.responses

data class DeleteOperationResponse(
    val matched: Int,
    val deleted: Int,
)
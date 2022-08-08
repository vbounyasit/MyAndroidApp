package com.example.mykotlinapp.network.dto.requests.user.contact

data class RemoveContactIdRequest(
    val senderRemoteId: String? = null,
    val receiverRemoteId: String? = null,
    val contactRemoteId: String? = null,
)
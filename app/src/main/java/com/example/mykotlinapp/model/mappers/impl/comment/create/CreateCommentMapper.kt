package com.example.mykotlinapp.model.mappers.impl.comment.create

import com.example.mykotlinapp.model.dto.inputs.form.comment.CreateCommentInput
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.comment.CreateCommentRequest

object CreateCommentMapper : NetworkRequestMapper<CreateCommentInput, CreateCommentRequest> {
    override fun toNetworkRequest(entity: CreateCommentInput): CreateCommentRequest {
        return CreateCommentRequest(entity.content, entity.parentRemoteId)
    }
}
package com.example.mykotlinapp.model.mappers.impl.post.create

import com.example.mykotlinapp.model.dto.inputs.form.post.CreatePostInput
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.post.CreatePostRequest

object CreatePostMapper : NetworkRequestMapper<CreatePostInput, CreatePostRequest> {
    override fun toNetworkRequest(entity: CreatePostInput): CreatePostRequest {
        return CreatePostRequest(entity.content, null)//todo
    }
}
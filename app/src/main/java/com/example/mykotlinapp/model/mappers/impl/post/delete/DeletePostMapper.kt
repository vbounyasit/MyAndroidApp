package com.example.mykotlinapp.model.mappers.impl.post.delete

import com.example.mykotlinapp.model.entity.post.UserPost
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.post.DeletePostRequest

object DeletePostMapper : NetworkRequestMapper<UserPost, DeletePostRequest> {
    override fun toNetworkRequest(entity: UserPost): DeletePostRequest {
        return DeletePostRequest(
            entity.remoteId,
            entity.groupRemoteId
        )
    }
}
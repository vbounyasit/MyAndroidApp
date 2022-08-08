package com.example.mykotlinapp.model.mappers.impl.comment.delete

import com.example.mykotlinapp.model.entity.post.UserComment
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.comment.DeleteCommentRequest

object DeleteCommentMapper : NetworkRequestMapper<UserComment, DeleteCommentRequest> {
    override fun toNetworkRequest(entity: UserComment): DeleteCommentRequest {
        return DeleteCommentRequest(
            entity.remoteId,
            entity.parentGroupRemoteId
        )
    }
}
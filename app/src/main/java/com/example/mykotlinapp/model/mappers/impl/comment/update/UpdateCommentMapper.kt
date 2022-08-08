package com.example.mykotlinapp.model.mappers.impl.comment.update

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentInput
import com.example.mykotlinapp.model.entity.post.UserComment
import com.example.mykotlinapp.model.mappers.InputUpdateMapper
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentRequest

object UpdateCommentMapper : InputUpdateMapper<UpdateCommentInput, UserComment>, NetworkRequestMapper<UserComment, UpdateCommentRequest> {
    override fun toLocalUpdateWithInput(inputData: UpdateCommentInput): (UserComment) -> UserComment {
        return { it.copy(content = inputData.content, syncState = SyncState.PENDING_UPDATE) }
    }

    override fun toNetworkRequest(entity: UserComment): UpdateCommentRequest {
        return UpdateCommentRequest(entity.remoteId, entity.parentGroupRemoteId, entity.content)
    }
}
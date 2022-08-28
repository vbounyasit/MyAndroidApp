package com.example.mykotlinapp.model.mappers.impl.comment.update

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentVoteInput
import com.example.mykotlinapp.model.entity.post.UserComment
import com.example.mykotlinapp.model.mappers.InputUpdateMapper
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentVoteRequest

object UpdateCommentVoteMapper : InputUpdateMapper<UpdateCommentVoteInput, UserComment>,
    NetworkRequestMapper<UserComment, UpdateCommentVoteRequest> {
    override fun toLocalUpdateWithInput(inputData: UpdateCommentVoteInput): (UserComment) -> UserComment {
        return {
            it.copy(
                voteState = inputData.voteState,
                votesCount = it.votesCount + inputData.votesCountDelta,
                syncState = SyncState.PENDING_UPDATE
            )
        }
    }

    override fun toNetworkRequest(entity: UserComment): UpdateCommentVoteRequest {
        return UpdateCommentVoteRequest(
            entity.remoteId,
            entity.parentGroupRemoteId,
            entity.voteState.value
        )
    }
}
package com.example.mykotlinapp.model.mappers.impl.post.update

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostVoteInput
import com.example.mykotlinapp.model.entity.post.UserPost
import com.example.mykotlinapp.model.mappers.InputUpdateMapper
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostVoteRequest

object UpdatePostVoteMapper : InputUpdateMapper<UpdatePostVoteInput, UserPost>,
    NetworkRequestMapper<UserPost, UpdatePostVoteRequest> {
    override fun toLocalUpdate(inputData: UpdatePostVoteInput): (UserPost) -> UserPost {
        return {
            it.copy(
                voteState = inputData.voteState,
                votesCount = it.votesCount + inputData.votesCountDelta,
                syncState = SyncState.PENDING_UPDATE
            )
        }
    }

    override fun toNetworkRequest(entity: UserPost): UpdatePostVoteRequest {
        return UpdatePostVoteRequest(entity.remoteId, entity.groupRemoteId, entity.voteState.value)
    }
}
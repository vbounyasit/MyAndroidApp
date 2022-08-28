package com.example.mykotlinapp.model.mappers.impl.post.update

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostInput
import com.example.mykotlinapp.model.entity.post.UserPost
import com.example.mykotlinapp.model.mappers.InputUpdateMapper
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostRequest

object UpdatePostMapper : InputUpdateMapper<UpdatePostInput, UserPost>,
    NetworkRequestMapper<UserPost, UpdatePostRequest> {
    override fun toLocalUpdateWithInput(inputData: UpdatePostInput): (UserPost) -> UserPost {
        return { it.copy(content = inputData.content, syncState = SyncState.PENDING_UPDATE) }
    }

    override fun toNetworkRequest(entity: UserPost): UpdatePostRequest {
        return UpdatePostRequest(entity.remoteId, entity.groupRemoteId, entity.content)
    }
}
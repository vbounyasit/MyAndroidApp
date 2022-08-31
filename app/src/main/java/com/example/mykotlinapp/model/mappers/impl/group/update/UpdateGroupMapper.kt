package com.example.mykotlinapp.model.mappers.impl.group.update

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateGroupInput
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.mappers.InputUpdateMapper
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.chat.UpdateGroupRequest

object UpdateGroupMapper :
    NetworkRequestMapper<GroupProperty, UpdateGroupRequest>,
    InputUpdateMapper<UpdateGroupInput, GroupProperty> {

    override fun toLocalUpdate(inputData: UpdateGroupInput): (GroupProperty) -> GroupProperty {
        return {
            it.copy(
                groupName = inputData.name ?: it.groupName,
                groupDescription = inputData.description,
                syncState = SyncState.PENDING_UPDATE
            )
        }
    }

    override fun toNetworkRequest(entity: GroupProperty): UpdateGroupRequest {
        return UpdateGroupRequest(
            entity.remoteId,
            entity.groupName,
            entity.groupDescription
        )
    }
}
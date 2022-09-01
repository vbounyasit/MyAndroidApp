package com.example.mykotlinapp.model.mappers.impl.user.update

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.inputs.form.user.UpdateUserInput
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.mappers.InputUpdateMapper2
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserRequest
import com.example.mykotlinapp.utils.TimeProvider

object UpdateUserMapper : NetworkRequestMapper<User, UpdateUserRequest>,
    InputUpdateMapper2<UpdateUserInput, User> {

    override fun toNetworkRequest(entity: User): UpdateUserRequest {
        return UpdateUserRequest(
            entity.email,
            entity.firstName,
            entity.lastName,
            entity.profilePicture,
            entity.description
        )
    }

    override fun toLocalUpdate(inputData: UpdateUserInput, timeProvider: TimeProvider): (User) -> User {
        return {
            it.copy(
                firstName = inputData.firstName,
                lastName = inputData.lastName,
                email = inputData.email,
                description = inputData.aboutMe,
                syncState = SyncState.PENDING_UPDATE,
                updateTime = timeProvider.provideCurrentTimeMillis()
            )
        }
    }
}
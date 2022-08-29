package com.example.mykotlinapp.model.mappers.impl.user

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.user.UserDTO
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.mappers.DTOMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.toCapitalized
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserResponse

object UserMapper : NetworkResponseMapper<UserResponse, User>, DTOMapper<User, UserDTO> {

    override fun toDTO(entity: User): UserDTO {
        return UserDTO(
            entity.email.toCapitalized(),
            entity.firstName.toCapitalized(),
            entity.lastName.toCapitalized(),
            "${entity.firstName} ${entity.lastName}".toCapitalized(),
            entity.profilePicture,
            entity.profileBackgroundPicture,
            entity.description,
            entity.gender,
            entity.age
        )
    }

    override fun toEntity(networkData: UserResponse): User {
        return User(
            networkData.remoteId,
            networkData.email,
            networkData.firstName,
            networkData.lastName,
            networkData.profilePicture,
            networkData.profileBackgroundPicture,
            networkData.description,
            networkData.gender,
            networkData.age,
            SyncState.UP_TO_DATE
        )
    }

}
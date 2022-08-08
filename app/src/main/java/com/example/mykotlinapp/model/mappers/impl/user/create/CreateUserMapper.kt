package com.example.mykotlinapp.model.mappers.impl.user.create

import com.example.mykotlinapp.domain.pojo.Language
import com.example.mykotlinapp.model.dto.inputs.form.user.CreateUserInput
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.network.dto.requests.user.NewUserSettingsRequest
import com.example.mykotlinapp.network.dto.requests.user.SignUserUpRequest

object CreateUserMapper : NetworkRequestMapper<CreateUserInput, SignUserUpRequest> {
    override fun toNetworkRequest(entity: CreateUserInput): SignUserUpRequest {
        return SignUserUpRequest(
            entity.email,
            entity.username,
            entity.password,
            entity.firstName,
            entity.lastName,
            entity.birthday,
            entity.gender,
            NewUserSettingsRequest(Language.ENGLISH)
        )
    }
}
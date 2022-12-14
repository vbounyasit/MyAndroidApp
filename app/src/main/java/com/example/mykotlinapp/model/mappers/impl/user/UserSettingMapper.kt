package com.example.mykotlinapp.model.mappers.impl.user

import com.example.mykotlinapp.model.entity.user.UserSettingsPreference
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserSettingsRequest
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserSettingsResponse

object UserSettingMapper :
    NetworkRequestMapper<UserSettingsPreference, UpdateUserSettingsRequest>,
    NetworkResponseMapper<UserSettingsResponse, UserSettingsPreference> {

    override fun toEntity(networkData: UserSettingsResponse): UserSettingsPreference {
        return UserSettingsPreference(
            networkData.language,
            networkData.eventPushNotifications,
            networkData.chatPushNotifications
        )
    }

    override fun toNetworkRequest(entity: UserSettingsPreference): UpdateUserSettingsRequest {
        return UpdateUserSettingsRequest(
            entity.language,
            entity.eventPushNotifications,
            entity.chatPushNotifications
        )
    }
}
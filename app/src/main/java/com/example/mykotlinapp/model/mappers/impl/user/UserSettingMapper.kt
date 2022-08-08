package com.example.mykotlinapp.model.mappers.impl.user

import android.content.Context
import android.content.SharedPreferences.Editor
import com.example.mykotlinapp.model.entity.user.UserSettingsPreference
import com.example.mykotlinapp.model.mappers.NetworkRequestMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserSettingsRequest
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserSettingsResponse

class UserSettingMapper(val context: Context) :
    NetworkRequestMapper<UserSettingsPreference, UpdateUserSettingsRequest>,
    NetworkResponseMapper<UserSettingsResponse, (Editor) -> Editor> {

    override fun toEntity(networkData: UserSettingsResponse): (Editor) -> Editor {
        return UserSettingsPreference(
            networkData.language,
            networkData.eventPushNotifications,
            networkData.chatPushNotifications
        ).getSharedPrefEdits(context)
    }

    override fun toNetworkRequest(entity: UserSettingsPreference): UpdateUserSettingsRequest {
        return UpdateUserSettingsRequest(
            entity.language,
            entity.eventPushNotifications,
            entity.chatPushNotifications
        )
    }
}
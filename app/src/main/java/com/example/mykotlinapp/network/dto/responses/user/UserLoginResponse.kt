package com.example.mykotlinapp.network.dto.responses.user

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserChatSettingsResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserGroupSettingsResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserPostSettingsResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserSettingsResponse

data class UserLoginResponse(
    val user: UserResponse,
    val settings: UserSettingsResponse,
    val postSettings: UserPostSettingsResponse,
    val chatSettings: UserChatSettingsResponse,
    val groupSettings: UserGroupSettingsResponse,
    val authToken: String,
)
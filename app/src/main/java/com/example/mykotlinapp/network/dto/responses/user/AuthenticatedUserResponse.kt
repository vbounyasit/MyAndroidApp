package com.example.mykotlinapp.network.dto.responses.user

import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserPostSettingsResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserSettingsResponse

class AuthenticatedUserResponse(
    val user: UserResponse,
    val settings: UserSettingsResponse,
    val postSettings: UserPostSettingsResponse,
    val chatSettings: UserResponses.UserChatSettingsResponse,
    val groupSettings: UserResponses.UserGroupSettingsResponse,
)
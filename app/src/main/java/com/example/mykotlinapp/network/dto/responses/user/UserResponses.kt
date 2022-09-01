package com.example.mykotlinapp.network.dto.responses.user

import com.example.mykotlinapp.domain.pojo.Gender
import com.example.mykotlinapp.domain.pojo.Language

object UserResponses {
    data class UserResponse(
        val remoteId: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val profilePicture: String,
        val profileBackgroundPicture: String?,
        val description: String?,
        val gender: Gender,
        val age: Int,
        val creationTimeStamp: Long,
        val updateTimeStamp: Long
    )

    data class UserSettingsResponse(
        val language: Language,
        val eventPushNotifications: Boolean,
        val chatPushNotifications: Boolean,
    )

    data class UserPostSettingsResponse(
        val notifications: List<String>,
        val stickied: List<String>,
        val saved: List<String>,
    )

    data class UserChatSettingsResponse(
        val notifications: List<String>,
        val stickied: List<String>,
    )

    data class UserGroupSettingsResponse(
        val notifications: List<String>,
        val pushNotifications: List<String>,
    )

    data class UserContactResponse(
        val remoteId: String,
        val firstName: String,
        val lastName: String,
        val fullName: String,
        val profilePicture: String,
        val description: String?,
        val lastActive: Long,
    )
}
package com.example.mykotlinapp.model.entity.user

import android.content.Context
import android.content.SharedPreferences.Editor
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.Language

data class UserSettingsPreference(
    val language: Language,
    val eventPushNotifications: Boolean,
    val chatPushNotifications: Boolean,
) {
    fun getSharedPrefEdits(context: Context): (Editor) -> Editor = {
        it
            .putString(context.getString(R.string.language_key), language.ordinal.toString())
            .putBoolean(context.getString(R.string.event_notifications_key), eventPushNotifications)
            .putBoolean(context.getString(R.string.chat_notifications_key), chatPushNotifications)
    }
}
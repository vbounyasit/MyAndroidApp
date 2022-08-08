package com.example.mykotlinapp.model.dao

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.preference.PreferenceManager
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.Language
import com.example.mykotlinapp.model.entity.user.UserSettingsPreference
import com.example.mykotlinapp.ui.components.SharedPreferenceLiveData.Companion.SharedPreferenceBooleanLiveData
import com.example.mykotlinapp.ui.components.SharedPreferenceLiveData.Companion.SharedPreferenceStringLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*

class SharedPreferenceDao(
    val context: Context,
    private val dispatcher: CoroutineDispatcher,
) {

    /**
     * Read
     */

    /**
     * @return The saved authenticated user remote id of empty string
     */
    suspend fun getAuthUserRemoteId(): String? = getString(context.getString(R.string.user_remote_id_key))

    /**
     * @return The saved authentication token
     */
    suspend fun getUserAuthToken(): String? = getString(context.getString(R.string.auth_token_key))

    /**
     * @return The saved user settings from default preferences
     */
    suspend fun getUserSettings(): UserSettingsPreference =
        withContext(dispatcher) {
            val languageIndex = getDefaultSharedPreferences().getString(context.getString(R.string.language_key), null)
            UserSettingsPreference(
                languageIndex?.let { Language.values()[it.toInt()] } ?: Language.ENGLISH,
                getDefaultSharedPreferences().getBoolean(context.getString(R.string.event_notifications_key), true),
                getDefaultSharedPreferences().getBoolean(context.getString(R.string.chat_notifications_key), true)
            )
        }

    /**
     * @return Whether we should automatically authenticate user on application launch
     */
    suspend fun shouldRememberMe(): Boolean = getBoolean(context.getString(R.string.remember_me_key))

    /**
     * @return Whether dark mode is enabled
     */
    suspend fun isDarkMode(): Boolean {
        if (!getSharedPreferences().contains(context.getString(R.string.dark_mode_key)))
            updateSharedPreferences { it.putBoolean(context.getString(R.string.dark_mode_key), true) }
        return getSharedPreferences().getBoolean(context.getString(R.string.dark_mode_key), false)
    }

    /**
     * Generates a unique device ID based on UUID class
     *
     * @return The unique device id
     */
    suspend fun getUniqueDeviceId(): String {
        val deviceIdKey = context.getString(R.string.unique_device_id_key)
        val savedDeviceId = getSharedPreferences().getString(deviceIdKey, null)
        savedDeviceId?.let { return it } ?: run {
            val newDeviceId = UUID.randomUUID().toString()
            updateSharedPreferences { it.putString(deviceIdKey, newDeviceId) }
            return newDeviceId
        }
    }

    /**
     * Update
     */

    /**
     * Update shared preferences
     *
     * @param changes The changes to be commited and saved to preferences
     */
    private suspend fun updateSharedPreferences(changes: (Editor) -> Editor) {
        withContext(dispatcher) {
            getSharedPreferences().updatePreferences(changes)
        }
    }

    /**
     * Update user settings
     *
     * @param changes The changes to user settings
     * @receiver
     */
    suspend fun updateDefaultSharedPreferences(changes: (Editor) -> Editor) =
        getDefaultSharedPreferences().updatePreferences(changes)


    /**
     * Update preferences
     *
     * @param changes
     * @receiver A given sharedPreference instance
     */
    private suspend fun SharedPreferences.updatePreferences(changes: (Editor) -> Editor) {
        withContext(dispatcher) {
            val editor: Editor = this@updatePreferences.edit()
            changes(editor)
            editor.apply()
        }
    }

    /**
     * Saves authentication metadata to SharedPreferences
     *
     * @param authToken The user authentication token
     * @param userRemoteId The user remote id
     * @param rememberMe Whether we should automatically authenticate with saved data on launch
     */
    suspend fun updateAuthenticatedUserPrefs(authToken: String, userRemoteId: String, rememberMe: Boolean) {
        updateSharedPreferences {
            it
                .putString(context.getString(R.string.auth_token_key), authToken)
                .putString(context.getString(R.string.user_remote_id_key), userRemoteId)
                .putBoolean(context.getString(R.string.remember_me_key), rememberMe)
        }
    }

    /**
     * Update dark mode
     *
     * @param darkMode Whether to enable darkmode or not
     */
    suspend fun updateDarkMode(darkMode: Boolean) {
        updateSharedPreferences { editor ->
            editor.putBoolean(context.getString(R.string.dark_mode_key), darkMode)
        }
    }

    /**
     * Delete
     */

    suspend fun clearAuthenticationUser() {
        updateSharedPreferences {
            it
                .remove(context.getString(R.string.auth_token_key))
                .remove(context.getString(R.string.user_remote_id_key))
                .remove(context.getString(R.string.remember_me_key))
        }
    }

    /**
     * Authenticated http requests
     */

    /**
     * Performs an action (HTTP request) with locally saved authentication token and retrieves the result
     *
     * @param T The result type
     * @param action The action to perform to get the result
     * @return A success result with the output data or a failure result
     */
    suspend fun <T> getAPIAuthenticatedResult(action: suspend (String) -> T): Result<T> {
        return getUserAuthToken()?.let {
            try {
                Result.success(action("Bearer $it"))
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        } ?: Result.failure(Exception("Failed to authenticate user"))
    }

    /**
     * Performs an action (HTTP request) with locally saved authentication token
     *
     * @param action The action to perform
     * @return Whether the action succeeded with the resulting data or failed
     */
    suspend fun performAPIAuthenticatedAction(action: suspend (String) -> Unit): Result<Unit> {
        return getUserAuthToken()?.let {
            try {
                Result.success(action("Bearer $it"))
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        } ?: Result.failure(Exception("Failed to authenticate user"))
    }

    /**
     * LiveData factory
     */

    /**
     * Generates livedata instance containing the saved User Remote Id from SharedPreferences
     */
    fun newUserRemoteIdLiveData() = SharedPreferenceStringLiveData(
        getSharedPreferences(),
        context.getString(R.string.user_remote_id_key),
        "")

    /**
     * Generates livedata instance containing the saved Dark Mode flag from SharedPreferences
     */
    fun newDarkModeLiveData() = SharedPreferenceBooleanLiveData(
        getSharedPreferences(),
        context.getString(R.string.dark_mode_key),
        true
    )

    /**
     * Gets the custom shared preferences instance
     */
    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(context.resources.getString(R.string.shared_pref), Context.MODE_PRIVATE)
    }

    /**
     * Gets the default shared preferences instance
     */
    private fun getDefaultSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * Gets shared preferences data in an asynchronous way
     */
    private suspend fun getString(key: String): String? =
        withContext(dispatcher) { getSharedPreferences().getString(key, null) }

    private suspend fun getBoolean(key: String): Boolean =
        withContext(dispatcher) { getSharedPreferences().getBoolean(key, false) }
}
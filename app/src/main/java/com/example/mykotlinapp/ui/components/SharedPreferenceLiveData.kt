package com.example.mykotlinapp.ui.components

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

/**
 * Class representing a specific shared preferences data inside a LiveData object for UI live updates
 *
 * @param T The type of data stored in the shared preferences
 * @property sharedPrefs The shared preferences containing the saved data
 * @property key The key of the stored data
 * @property defValue The default value in case the key is not found
 */
abstract class SharedPreferenceLiveData<T>(
    val sharedPrefs: SharedPreferences,
    private val key: String,
    private val defValue: T,
) : LiveData<T>() {

    companion object {
        /**
         * Shared preference LiveData for strings
         */
        class SharedPreferenceStringLiveData(
            sharedPrefs: SharedPreferences,
            key: String,
            defValue: String
        ) :
            SharedPreferenceLiveData<String>(sharedPrefs, key, defValue) {
            override fun getValueFromPreferences(key: String, defValue: String): String =
                sharedPrefs.getString(key, defValue) ?: defValue
        }

        /**
         * Shared preference LiveData for booleans
         */
        class SharedPreferenceBooleanLiveData(
            sharedPrefs: SharedPreferences,
            key: String,
            defValue: Boolean
        ) :
            SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
            override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean =
                sharedPrefs.getBoolean(key, defValue)
        }
    }

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) {
                value = getValueFromPreferences(key, defValue)
            }
        }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}
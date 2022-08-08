package com.example.mykotlinapp.ui.screens.side_nav.settings.prefs

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.mykotlinapp.R

class UserSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_settings, rootKey)
    }
}
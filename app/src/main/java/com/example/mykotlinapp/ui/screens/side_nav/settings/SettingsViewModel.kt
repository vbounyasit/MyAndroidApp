package com.example.mykotlinapp.ui.screens.side_nav.settings

import androidx.work.WorkManager
import com.example.mykotlinapp.UPDATE_USER_SETTINGS_WORK_NAME
import com.example.mykotlinapp.background.workmanager.user.UpdateUserSettingsWorker
import com.example.mykotlinapp.ui.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(val workManager: WorkManager) : AppViewModel() {

    fun launchUserSettingsWork() = workManager.launchNetworkBackgroundTask<UpdateUserSettingsWorker>(BackgroundWorkConfig.UniqueBackgroundTask(UPDATE_USER_SETTINGS_WORK_NAME))

}
package com.example.mykotlinapp.ui.screens.side_nav.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.mykotlinapp.UPDATE_USER_WORK_NAME
import com.example.mykotlinapp.background.workmanager.user.UpdateUserWorker
import com.example.mykotlinapp.model.dto.inputs.form.user.UpdateUserInput
import com.example.mykotlinapp.model.dto.ui.user.UserDTO
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.ui.AppViewModel
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.UniqueBackgroundTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    val userRepository: UserRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    private val _userRemoteId = userRepository.newUserRemoteIdLiveData()

    val userData: LiveData<UserDTO?> = _userRemoteId.switchMap {
        userRepository.getUserData(it).asLiveData()
    }

    fun sendUserProfileUpdate(updateUserInput: UpdateUserInput): Job {
        return viewModelScope.launch {
            userRepository.updateUser(updateUserInput)
            workManager.launchNetworkBackgroundTask<UpdateUserWorker>(
                UniqueBackgroundTask(
                    UPDATE_USER_WORK_NAME
                ), initialDelay = Duration.ofMinutes(1)
            )
        }
    }
}
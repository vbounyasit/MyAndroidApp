package com.example.mykotlinapp.ui.screens.chats.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.WorkManager
import com.example.mykotlinapp.CREATE_CONTACT_WORK_NAME
import com.example.mykotlinapp.REMOVE_CONTACTS_WORK_NAME
import com.example.mykotlinapp.WORK_CREATE_CONTACT_INPUT_KEY
import com.example.mykotlinapp.background.workmanager.contact.CreateContactWorker
import com.example.mykotlinapp.background.workmanager.contact.RemoveContactsWorker
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.ui.AppViewModel
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.UniqueBackgroundTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    val contacts: LiveData<List<UserContactDTO>> =
        userRepository.getUserContactsWithRequests().asLiveData()

    fun removeContactOrRequest(remoteId: String) {
        viewModelScope.launch {
            userRepository.submitContactForDeletion(remoteId)
            workManager.launchNetworkBackgroundTask<RemoveContactsWorker>(
                UniqueBackgroundTask(
                    REMOVE_CONTACTS_WORK_NAME
                )
            )
        }
    }

    fun acceptRequest(remoteId: String) {
        val inputData: Data = run {
            val builder = Data.Builder()
            builder.putString(WORK_CREATE_CONTACT_INPUT_KEY, remoteId)
            builder.build()
        }
        workManager.launchNetworkBackgroundTask<CreateContactWorker>(
            UniqueBackgroundTask(
                CREATE_CONTACT_WORK_NAME
            ), inputData
        )
    }

    fun retrieveData() = submitHttpRequest { userRepository.retrieveUserContacts() }

}
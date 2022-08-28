package com.example.mykotlinapp.ui.screens.chats.contacts.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.mykotlinapp.CREATE_CONTACT_WORK_NAME
import com.example.mykotlinapp.WORK_CREATE_CONTACT_INPUT_KEY
import com.example.mykotlinapp.background.workmanager.contact.CreateContactWorker
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.ui.AppViewModel
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.UniqueBackgroundTask
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactSearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    private val _foundContacts = MutableLiveData<List<UserContactDTO>?>()

    val foundContacts: LiveData<List<UserContactDTO>?> = _foundContacts
    val createContactWorkInfo: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkLiveData(CREATE_CONTACT_WORK_NAME)

    fun updateSearchResults(searchTag: String) =
        submitHttpRequest({ userRepository.retrieveUserContactSearchResults(searchTag) }) {
            _foundContacts.value = it
        }

    fun sendContactRequest(remoteId: String) {
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

    fun setOutgoingRequest(remoteId: String) {
        _foundContacts.value = _foundContacts.value?.map {
            if (it.remoteId == remoteId) it.copy(relationType = ContactRelationType.OUTGOING) else it
        }
    }

}
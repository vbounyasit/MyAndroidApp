package com.example.mykotlinapp.ui.screens.chats.history.chat.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.work.Data
import androidx.work.WorkManager
import com.example.mykotlinapp.CREATE_CONTACT_WORK_NAME
import com.example.mykotlinapp.WORK_CREATE_CONTACT_INPUT_KEY
import com.example.mykotlinapp.background_work.workmanager.contact.CreateContactWorker
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.ui.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatPageDialogViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    private val _chatRemoteId = MutableLiveData<String>()

    val chatRemoteId: LiveData<String> = _chatRemoteId

    val chatContactParticipant: LiveData<List<UserContactDTO>> = _chatRemoteId.switchMap {
        chatRepository.getContactParticipants(it).asLiveData()
    }

    fun updateChatRemoteId(chatRemoteId: String) {
        _chatRemoteId.value = chatRemoteId
    }

    fun sendContactRequest(remoteId: String) {
        val inputData: Data = run {
            val builder = Data.Builder()
            builder.putString(WORK_CREATE_CONTACT_INPUT_KEY, remoteId)
            builder.build()
        }
        workManager.launchNetworkBackgroundTask<CreateContactWorker>(BackgroundWorkConfig.UniqueBackgroundTask(CREATE_CONTACT_WORK_NAME), inputData)
    }
}
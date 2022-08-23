package com.example.mykotlinapp.ui.screens.chats.history.chat.logs

import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.mykotlinapp.CREATE_CHAT_LOGS_WORK_NAME
import com.example.mykotlinapp.background.workmanager.chat.CreateChatLogsWorker
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatLogInput
import com.example.mykotlinapp.model.dto.ui.chat.ChatBubbleDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatLogDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatParticipantDTO
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.ui.AppViewModel
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.UniqueBackgroundTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatLogsViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    private val _chatRemoteId = MutableLiveData<String>()

    val chatRemoteId: LiveData<String> = _chatRemoteId

    private val chatParticipants: LiveData<Pair<String, List<ChatParticipantDTO>>> = _chatRemoteId.switchMap { remoteId ->
        chatRepository.getChatParticipants(remoteId).asLiveData().map { Pair(remoteId, it) }
    }

    val chatBubbles: LiveData<List<ChatBubbleDTO>> = chatParticipants.switchMap {
        val (remoteId, participants) = it
        chatRepository.getChatBubbles(remoteId).map { bubbles ->
            bubbles.mapIndexed { index, bubble ->
                if (bubble is ChatLogDTO) {
                    val nextBubbleTime = if (index < bubbles.size - 1) bubbles[index + 1].creationTimeStamp else null
                    bubble.copy(
                        readBy = participants
                            .filter { participant ->
                                val hasReadNext = participant.lastReadTime?.let { nextBubbleTime?.let { participant.lastReadTime > nextBubbleTime } } ?: false
                                val hasRead = participant.lastReadTime?.let { readTime -> readTime > bubble.creationTimeStamp } ?: false
                                hasRead && !hasReadNext
                            }
                    )
                } else bubble
            }
        }.asLiveData()
    }

    fun createChatLog(content: String) {
        chatRemoteId.value?.let { remoteId ->
            viewModelScope.launch {
                chatRepository.createChatLog(CreateChatLogInput(remoteId, content))
                workManager.launchNetworkBackgroundTask<CreateChatLogsWorker>(UniqueBackgroundTask(CREATE_CHAT_LOGS_WORK_NAME))
            }
        }
    }

    fun updateChatRemoteId(chatRemoteId: String) {
        _chatRemoteId.value = chatRemoteId
    }

    fun readChat() {
        chatRemoteId.value?.let { submitHttpRequest { chatRepository.sendReadChat(it) } }
    }


}
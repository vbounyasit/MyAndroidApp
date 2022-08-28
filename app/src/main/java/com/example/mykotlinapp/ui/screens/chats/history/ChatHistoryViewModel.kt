package com.example.mykotlinapp.ui.screens.chats.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mykotlinapp.model.dto.ui.chat.ChatListItemDTO
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.ui.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHistoryViewModel @Inject constructor(private val chatRepository: ChatRepository) :
    AppViewModel() {

    private val _navigateToGroupChatId = MutableLiveData<String?>()

    val chats: LiveData<List<ChatListItemDTO>> = chatRepository.getChatItems().asLiveData()

    fun retrieveData() = submitHttpRequest { chatRepository.retrieveChatItems() }

    val navigateToGroupChat: LiveData<String?> = _navigateToGroupChatId

    fun onChatClicked(chatRemoteId: String) {
        viewModelScope.launch {
            if (chatRepository.retrieveChatData(chatRemoteId).isSuccess)
                _navigateToGroupChatId.value = chatRemoteId
        }
    }

    fun onNavigatedToGroupChat() {
        _navigateToGroupChatId.value = null
    }
}
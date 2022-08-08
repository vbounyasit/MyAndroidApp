package com.example.mykotlinapp.ui.screens.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatInput
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.ui.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : AppViewModel() {

    private val _navigateToContactSearchPage = MutableLiveData<Boolean>()
    private val _navigateToCreatedChatId = MutableLiveData<String?>()

    val navigateToContactSearchPage: LiveData<Boolean> = _navigateToContactSearchPage
    val navigateToCreatedChatId: LiveData<String?> = _navigateToCreatedChatId

    fun createNewChat(createChatInput: CreateChatInput): Job {
        return submitHttpRequest({ chatRepository.createChat(createChatInput) }) {
            navigateToCreatedChat(it)
        }
    }

    fun navigateToContactSearch() {
        _navigateToContactSearchPage.value = true
    }

    fun onNavigateToContactSearch() {
        _navigateToContactSearchPage.value = false
    }

    private fun navigateToCreatedChat(remoteId: String) {
        _navigateToCreatedChatId.value = remoteId
    }

    fun onNavigatedToCreatedChat() {
        _navigateToCreatedChatId.value = null
    }

}
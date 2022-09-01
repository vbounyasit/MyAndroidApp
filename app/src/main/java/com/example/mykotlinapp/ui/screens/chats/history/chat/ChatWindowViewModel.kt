package com.example.mykotlinapp.ui.screens.chats.history.chat

import androidx.lifecycle.*
import com.example.mykotlinapp.model.dto.ui.chat.ChatDTO
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.model.repository.impl.GroupRepository
import com.example.mykotlinapp.model.repository.impl.PostRepository
import com.example.mykotlinapp.ui.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatWindowViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val groupRepository: GroupRepository,
    private val postRepository: PostRepository,
) : AppViewModel() {

    private val _chatRemoteId = MutableLiveData<String>()

    var tabSelected: Int? = null

    val chatRemoteId: LiveData<String> = _chatRemoteId

    val chatData: LiveData<ChatDTO?> = _chatRemoteId.switchMap {
        chatRepository.getChatFlow(it).asLiveData()
    }

    val groupRemoteId: LiveData<String?> = chatData.distinctUntilChanged().map {
        it?.let(ChatDTO::groupRemoteId)
    }

    val chatMetaData: LiveData<ChatMetaData?> = chatData.distinctUntilChanged().map {
        it?.let {
            ChatMetaData(
                it.remoteId,
                it.groupRemoteId,
                it.isAdmin,
                it.profilePictures.size > 1
            )
        }
    }

    val unreadChatLogsCount: LiveData<Int> = _chatRemoteId.switchMap {
        chatRepository.getUnreadChatLogsCount(it).asLiveData()
    }

    val unreadPostsCount: LiveData<Int> = groupRemoteId.switchMap { remoteId ->
        remoteId?.let { groupRepository.getUnreadPostsCount(it).asLiveData() } ?: liveData { emit(0) }
    }

    fun updateChatRemoteId(chatRemoteId: String) {
        _chatRemoteId.value = chatRemoteId
    }

    fun retrieveChatLogs(chatRemoteId: String) =
        submitHttpRequest { chatRepository.retrieveChatBubbles(chatRemoteId) }

    fun retrievePosts(groupRemoteId: String) =
        submitHttpRequest { postRepository.retrievePostList(groupRemoteId) }

    data class ChatMetaData(
        val remoteId: String,
        val groupRemoteId: String,
        val isAdmin: Boolean,
        val isGroupChat: Boolean
    )

}
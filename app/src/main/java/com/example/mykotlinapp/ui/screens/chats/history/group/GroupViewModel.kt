package com.example.mykotlinapp.ui.screens.chats.history.group

import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.mykotlinapp.UPDATE_GROUP_WORK_NAME
import com.example.mykotlinapp.background.workmanager.chat.UpdateGroupWorker
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateGroupInput
import com.example.mykotlinapp.model.dto.inputs.form.post.CreatePostInput
import com.example.mykotlinapp.model.dto.ui.chat.ChatParticipantDTO
import com.example.mykotlinapp.model.dto.ui.group.GroupDTO
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.model.repository.impl.GroupRepository
import com.example.mykotlinapp.model.repository.impl.PostRepository
import com.example.mykotlinapp.ui.AppViewModel
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.UniqueBackgroundTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val chatRepository: ChatRepository,
    private val postRepository: PostRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    private val _groupRemoteId = MutableLiveData<String>()
    private val _navigateToCreatedPostId = MutableLiveData<String?>()

    val groupRemoteId: LiveData<String> = _groupRemoteId
    val navigateToCreatedPostId: LiveData<String?> = _navigateToCreatedPostId

    val groupWindow: LiveData<GroupDTO?> = _groupRemoteId.switchMap {
        groupRepository.getGroup(it).asLiveData()
    }

    val groupParticipants: LiveData<List<ChatParticipantDTO>?> = groupWindow.switchMap {
        it?.let { group -> chatRepository.getChatParticipants(group.chatRemoteId).asLiveData() }
            ?: liveData { listOf<ChatParticipantDTO>() }
    }

    fun updateGroupRemoteId(groupRemoteId: String) {
        _groupRemoteId.value = groupRemoteId
    }

    fun sendGroupUpdate(updateGroupInput: UpdateGroupInput): Job {
        val result = viewModelScope.launch {
            groupRepository.updateGroup(updateGroupInput)
            workManager.launchNetworkBackgroundTask<UpdateGroupWorker>(
                UniqueBackgroundTask(
                    UPDATE_GROUP_WORK_NAME
                )
            )
        }
        return result
    }

    fun createGroupPost(groupRemoteId: String, createPostInput: CreatePostInput): Job {
        return submitHttpRequest({
            postRepository.sendCreatePost(
                groupRemoteId,
                createPostInput
            )
        }) { postRemoteId ->
            submitHttpRequest({ postRepository.retrievePost(groupRemoteId, postRemoteId) }) {
                navigateToCreatedPost(postRemoteId)
            }
        }
    }

    fun readGroup(groupRemoteId: String) {
        viewModelScope.launch { groupRepository.sendReadGroup(groupRemoteId) }
    }

    private fun navigateToCreatedPost(remoteId: String) {
        _navigateToCreatedPostId.value = remoteId
    }

    fun onNavigatedToCreatedPost() {
        _navigateToCreatedPostId.value = null
    }

}
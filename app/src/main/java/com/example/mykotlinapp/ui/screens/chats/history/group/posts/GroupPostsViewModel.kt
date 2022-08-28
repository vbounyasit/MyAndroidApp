package com.example.mykotlinapp.ui.screens.chats.history.group.posts

import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.mykotlinapp.REMOVE_POSTS_WORK_NAME
import com.example.mykotlinapp.UPDATE_POSTS_WORK_NAME
import com.example.mykotlinapp.UPDATE_POST_VOTE_STATES_WORK_NAME
import com.example.mykotlinapp.background.workmanager.post.RemovePostsWorker
import com.example.mykotlinapp.background.workmanager.post.UpdatePostVoteStatesWorker
import com.example.mykotlinapp.background.workmanager.post.UpdatePostsWorker
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostVoteInput
import com.example.mykotlinapp.model.dto.inputs.ui_item.impl.UpdatePostNotificationInputUI
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.model.repository.impl.GroupRepository
import com.example.mykotlinapp.model.repository.impl.PostRepository
import com.example.mykotlinapp.ui.AppViewModel
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.UniqueBackgroundTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class GroupPostsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val postRepository: PostRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    private val _groupRemoteId = MutableLiveData<String>()
    private val _navigateToPostId = MutableLiveData<String?>()

    val groupRemoteId: LiveData<String> = _groupRemoteId
    val navigateToUserPostId: LiveData<String?> = _navigateToPostId

    val groupPosts: LiveData<List<PostDTO>> = _groupRemoteId.switchMap {
        postRepository.getUserPosts(it).asLiveData()
    }

    fun retrieveData(groupRemoteId: String) =
        submitHttpRequest { postRepository.retrievePostList(groupRemoteId) }

    fun editPost(updatePostInput: UpdatePostInput): Job {
        return viewModelScope.launch {
            postRepository.updatePost(updatePostInput)
            workManager.launchNetworkBackgroundTask<UpdatePostsWorker>(
                UniqueBackgroundTask(
                    UPDATE_POSTS_WORK_NAME
                ), initialDelay = Duration.ofMinutes(1)
            )
        }
    }

    val isGroupAdmin: LiveData<Boolean> = _groupRemoteId.switchMap {
        liveData { emit(groupRepository.isGroupAdmin(it)) }
    }

    fun votePost(updatePostVoteInput: UpdatePostVoteInput) {
        viewModelScope.launch {
            postRepository.updatePostVoteState(updatePostVoteInput)
            workManager.launchNetworkBackgroundTask<UpdatePostVoteStatesWorker>(
                UniqueBackgroundTask(
                    UPDATE_POST_VOTE_STATES_WORK_NAME
                ), initialDelay = Duration.ofMinutes(3)
            )
        }
    }

    fun deletePost(remoteId: String) {
        viewModelScope.launch {
            postRepository.submitPostForDeletion(remoteId)
            workManager.launchNetworkBackgroundTask<RemovePostsWorker>(
                UniqueBackgroundTask(
                    REMOVE_POSTS_WORK_NAME
                )
            )
        }
    }

    fun updateNotifications(updatePostNotificationInputUI: UpdatePostNotificationInputUI) {
        viewModelScope.launch { postRepository.updatePostNotifications(updatePostNotificationInputUI) }
    }

    fun updateGroupRemoteId(groupRemoteId: String) {
        _groupRemoteId.value = groupRemoteId
    }

    fun navigateToPost(postRemoteId: String) {
        _navigateToPostId.value = postRemoteId
    }

    fun onNavigatedToUserPost() {
        _navigateToPostId.value = null
    }
}
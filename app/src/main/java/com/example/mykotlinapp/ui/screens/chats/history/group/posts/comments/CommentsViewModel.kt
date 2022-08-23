package com.example.mykotlinapp.ui.screens.chats.history.group.posts.comments

import androidx.lifecycle.*
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.mykotlinapp.*
import com.example.mykotlinapp.background.workmanager.post.*
import com.example.mykotlinapp.model.dto.inputs.form.comment.CreateCommentInput
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentInput
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentVoteInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostVoteInput
import com.example.mykotlinapp.model.dto.inputs.ui_item.impl.UpdatePostNotificationInputUI
import com.example.mykotlinapp.model.dto.ui.post.CommentDTO
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.model.dto.ui.post.PostGroupData
import com.example.mykotlinapp.model.repository.impl.CommentRepository
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
class CommentsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val workManager: WorkManager,
) : AppViewModel() {

    private var _postRemoteId = MutableLiveData<String>()
    private var _groupRemoteId = MutableLiveData<String>()
    private var _isHeaderPostDeleted = MutableLiveData<Boolean>()
    private var _shouldReloadAdapter = MutableLiveData<Boolean>()

    val isHeaderPostDeleted: LiveData<Boolean> = _isHeaderPostDeleted

    val shouldReloadAdapter: LiveData<Boolean> = _shouldReloadAdapter

    val groupData: LiveData<PostGroupData?> = _groupRemoteId.switchMap {
        groupRepository.getUserPostAppBar(it).asLiveData()
    }

    val userPost: LiveData<PostDTO?> = _postRemoteId.switchMap {
        postRepository.getUserPost(it).asLiveData()
    }

    val userComments: LiveData<List<CommentDTO>> = _postRemoteId.switchMap {
        commentRepository.getUserPostComments(it).asLiveData()
    }

    val removeCommentsWorkInfo: LiveData<List<WorkInfo>> = workManager.getWorkInfosForUniqueWorkLiveData(REMOVE_COMMENTS_WORK_NAME)

    private val isGroupAdmin: LiveData<Boolean> = _groupRemoteId.switchMap {
        liveData { emit(groupRepository.isGroupAdmin(it)) }
    }
    private val isPostCreator: LiveData<Boolean> = _postRemoteId.switchMap {
        liveData { emit(postRepository.isPostCreator(it)) }
    }

    fun retrieveData(groupRemoteId: String, postRemoteId: String) =
        submitHttpRequest { commentRepository.retrieveCommentList(groupRemoteId, postRemoteId) }

    val commentsAdminPrivilege = run {
        val mediatorLiveData = MediatorLiveData<Pair<Boolean?, Boolean?>>()
        mediatorLiveData.addSource(isGroupAdmin) {
            it?.let { isAdmin -> mediatorLiveData.value = Pair(isAdmin, mediatorLiveData.value?.second) }
        }
        mediatorLiveData.addSource(isPostCreator) {
            it?.let { isCreator -> mediatorLiveData.value = Pair(mediatorLiveData.value?.first, isCreator) }
        }
        mediatorLiveData
    }

    /**
     * Header post
     */

    fun editPost(updatePostInput: UpdatePostInput): Job {
        return viewModelScope.launch {
            postRepository.updatePost(updatePostInput)
            workManager.launchNetworkBackgroundTask<UpdatePostsWorker>(UniqueBackgroundTask(UPDATE_POSTS_WORK_NAME))
        }
    }

    fun votePost(updatePostVoteInput: UpdatePostVoteInput) {
        viewModelScope.launch {
            postRepository.updatePostVoteState(updatePostVoteInput)
            workManager.launchNetworkBackgroundTask<UpdatePostVoteStatesWorker>(UniqueBackgroundTask(UPDATE_POST_VOTE_STATES_WORK_NAME), initialDelay = Duration.ofMinutes(3))
        }
    }

    fun deletePost(remoteId: String) {
        viewModelScope.launch {
            postRepository.submitPostForDeletion(remoteId)
            workManager.launchNetworkBackgroundTask<RemovePostsWorker>(UniqueBackgroundTask(REMOVE_POSTS_WORK_NAME), initialDelay = Duration.ofMinutes(1))
        }
    }

    //todo
    fun updateNotifications(updatePostNotificationInputUI: UpdatePostNotificationInputUI) {

    }

    /**
     * Comments
     */

    fun createComment(groupRemoteId: String, postRemoteId: String, createCommentInput: CreateCommentInput): Job {
        return submitHttpRequest({ commentRepository.sendCreateComment(groupRemoteId, postRemoteId, createCommentInput) }) {
            submitHttpRequest({ commentRepository.retrieveCommentList(groupRemoteId, postRemoteId) }) {
                reloadAdapter()
            }
        }
    }

    fun editComment(updateCommentInput: UpdateCommentInput): Job {
        val result = viewModelScope.launch {
            commentRepository.updateComment(updateCommentInput)
            workManager.launchNetworkBackgroundTask<UpdateCommentsWorker>(UniqueBackgroundTask(UPDATE_COMMENTS_WORK_NAME), initialDelay = Duration.ofMinutes(1))
        }
        return result
    }

    fun voteComment(updateCommentVoteInput: UpdateCommentVoteInput) {
        viewModelScope.launch {
            commentRepository.updateCommentVoteState(updateCommentVoteInput)
            workManager.launchNetworkBackgroundTask<UpdateCommentVoteStatesWorker>(UniqueBackgroundTask(UPDATE_COMMENT_VOTE_STATES_WORK_NAME), initialDelay = Duration.ofMinutes(3))
        }
    }

    fun deleteComment(remoteId: String) {
        viewModelScope.launch {
            commentRepository.submitCommentForDeletion(remoteId)
            workManager.launchNetworkBackgroundTask<RemoveCommentsWorker>(UniqueBackgroundTask(REMOVE_COMMENTS_WORK_NAME), initialDelay = Duration.ofMinutes(1))
        }
    }

    fun onCommentsDeletion() {
        if (_groupRemoteId.value != null && _postRemoteId.value != null)
            submitHttpRequest({ commentRepository.retrieveCommentList(_groupRemoteId.value!!, _postRemoteId.value!!) }) {
                reloadAdapter()
            }
    }

    private fun reloadAdapter() {
        _shouldReloadAdapter.value = true
    }

    fun onReloadAdapter() {
        _shouldReloadAdapter.value = false
    }

    fun onHeaderPostDeletion() {
        _isHeaderPostDeleted.value = false
    }


    fun updatePostRemoteId(postRemoteId: String) {
        _postRemoteId.value = postRemoteId
    }

    fun updateGroupRemoteId(groupRemoteId: String) {
        _groupRemoteId.value = groupRemoteId
    }
}
package com.example.mykotlinapp.model.repository.impl

import android.content.Context
import com.example.mykotlinapp.di.Qualifiers.IoDispatcher
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.PostDao
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.inputs.form.post.CreatePostInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostVoteInput
import com.example.mykotlinapp.model.dto.inputs.ui_item.impl.UpdatePostNotificationInputUI
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.model.mappers.impl.post.PostMapper
import com.example.mykotlinapp.model.mappers.impl.post.PostMediasMapper
import com.example.mykotlinapp.model.mappers.impl.post.create.CreatePostMapper
import com.example.mykotlinapp.model.mappers.impl.post.delete.DeletePostMapper
import com.example.mykotlinapp.model.mappers.impl.post.update.UpdatePostMapper
import com.example.mykotlinapp.model.mappers.impl.post.update.UpdatePostVoteMapper
import com.example.mykotlinapp.model.repository.AppRepository
import com.example.mykotlinapp.network.dto.requests.post.CreatePostRequest
import com.example.mykotlinapp.network.dto.requests.post.DeletePostRequest
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostRequest
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostVoteRequest
import com.example.mykotlinapp.network.dto.responses.CreateOperationResponse
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.post.PostResponse
import com.example.mykotlinapp.network.service.PostApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    sharedPreferenceDao: SharedPreferenceDao,
    private val postApiService: PostApiService,
    private val postDao: PostDao,
) : AppRepository(sharedPreferenceDao) {

    /**
     * Retrieve local data
     */

    /**
     * Gets the list of posts to display for a given group
     *
     * @param groupRemoteId The remote id of the group to get posts from
     * @return A flow containing the list of posts
     */
    fun getUserPosts(groupRemoteId: String): Flow<List<PostDTO>> = postDao.getUserPostsWithImagesFlow(groupRemoteId).toDTO((PostMapper::toDTO)(context))

    /**
     * Gets a user post to display
     *
     * @param remoteId The remote id of the post
     * @return A flow containing the post to display
     */
    fun getUserPost(remoteId: String): Flow<PostDTO?> =
        postDao.getUserPostWithImagesFlow(remoteId)
            .toDTO((PostMapper::toDTO)(context))
            .map { it.firstOrNull() }
            .distinctUntilChanged()

    /**
     * @param remoteId The remote id of the given post
     * @return Whether the user is a given post creator
     */
    suspend fun isPostCreator(remoteId: String): Boolean = postDao.isCreator(remoteId) ?: false

    /**
     * Database
     */

    /**
     * Updates a local post
     *
     * @param updatePostInput The update post form input
     */
    suspend fun updatePost(updatePostInput: UpdatePostInput): Result<Unit> =
        executeAction(dispatcher) {
            postDao.getUserPost(updatePostInput.remoteId)
                ?.let(UpdatePostMapper.toLocalUpdate(updatePostInput))
                ?.let { postDao.update(it) }
        }

    /**
     * Updates a local post vote state
     *
     * @param voteInput The post vote input
     */
    suspend fun updatePostVoteState(voteInput: UpdatePostVoteInput): Result<Unit> =
        executeAction(dispatcher) {
            postDao.getUserPost(voteInput.remoteId)
                ?.let(UpdatePostVoteMapper.toLocalUpdate(voteInput))
                ?.let { postDao.update(it) }
        }

    //todo
    suspend fun updatePostNotifications(notification: UpdatePostNotificationInputUI) {

    }

    /**
     * Submits a post for later deletion through an API request
     *
     * @param postRemoteId The remote id of the post to submit for deletion
     */
    suspend fun submitPostForDeletion(postRemoteId: String): Result<Unit> =
        executeAction(dispatcher) {
            postDao.getUserPost(postRemoteId)?.let {
                postDao.update(it.copy(syncState = SyncState.PENDING_REMOVAL))
            }
        }

    /**
     * Updates a local post with the response received from the API
     *
     * @param postResponse The API response
     */
    private suspend fun updatePostFromResponse(postResponse: PostResponse) =
        updateEntity(
            PostMapper.toEntity(postResponse),
            DataOperations(
                postDao::getUserPost,
                postDao::insert
            )
        )

    /**
     * Updates local posts with the response received from the API
     *
     * @param postListResponse The API response
     */
    private suspend fun updatePostsFromResponse(postListResponse: List<PostResponse>) =
        updateData(
            postListResponse.map(PostMapper::toEntity),
            DataBulkOperations(
                postDao::getUserPostsByIds,
                postDao::insertUserPosts,
                postDao::clearPostsNotIn
            )
        )

    /**
     * Updates local post medias with the response received from the API
     *
     * @param postListResponse The API response
     */
    private suspend fun updatePostMediasFromResponse(postListResponse: List<PostResponse>) =
        replaceLocalData(
            postListResponse.flatMap(PostMediasMapper::toEntity),
            DataBulkOperations(
                postDao::getPostMediasByIds,
                postDao::insertPostMedias,
                postDao::clearPostMediasNotIn
            )
        )

    /**
     * Network
     */

    /**
     * Create
     */

    /**
     * Sends a post creation request to the API from the creation form input
     *
     * @param groupRemoteId The remote id of the group to create the post in
     * @param createPostInput The post creation form input
     * @returnThe newly created post remote id
     */
    suspend fun sendCreatePost(groupRemoteId: String, createPostInput: CreatePostInput): Result<String> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val request: CreatePostRequest = CreatePostMapper.toNetworkRequest(createPostInput)
            val response: CreateOperationResponse = postApiService.createPost(authHeader, groupRemoteId, request)
            response.remoteId
        }

    /**
     * Read
     */

    /**
     * Retrieves the post list from the API and saves them to the local database
     *
     * @param groupRemoteId The remote id of the group to retrieve the post for
     * @param postRemoteId The remote id of the post to retrieve
     * @return A success or failure result
     */
    suspend fun retrievePost(groupRemoteId: String, postRemoteId: String): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val postResponse: PostResponse = postApiService.getPostData(authHeader, groupRemoteId, postRemoteId)
            updatePostFromResponse(postResponse)
        }

    /**
     * Retrieves the post list from the API and saves them to the local database
     *
     * @param groupRemoteId The remote id of the group to retrieve the posts from
     * @return A success or failure result
     */
    suspend fun retrievePostList(groupRemoteId: String): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val postListResponse: List<PostResponse> = postApiService.getPostList(authHeader, groupRemoteId)
            updatePostsFromResponse(postListResponse)
            updatePostMediasFromResponse(postListResponse)
        }

    /**
     * Update
     */

    /**
     * Sends an update requests to the API for the locally updated posts
     *
     * @return A success or failure result
     */
    suspend fun sendUpdatePosts(): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val postsToUpdate = postDao.getPostsBySyncState(SyncState.PENDING_UPDATE)
            if (postsToUpdate.isNotEmpty()) {
                val request: List<UpdatePostRequest> = postsToUpdate.map { UpdatePostMapper.toNetworkRequest(it) }
                val response: UpdateOperationResponse = postApiService.updatePosts(authHeader, request)
                if (response.modified == postsToUpdate.size)
                    postDao.update(postsToUpdate.map { it.copy(syncState = SyncState.UP_TO_DATE) })
            }
        }

    /**
     * Sends an update request to the API for the locally updated post votes
     *
     * @return A success or failure result
     */
    suspend fun sendUpdatePostVoteStates(): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val postsToUpdate = postDao.getPostsBySyncState(SyncState.PENDING_UPDATE)
            if (postsToUpdate.isNotEmpty()) {
                val request: List<UpdatePostVoteRequest> = postsToUpdate.map { UpdatePostVoteMapper.toNetworkRequest(it) }
                val response: UpdateOperationResponse = postApiService.updatePostVotes(authHeader, request)
                if (response.modified == postsToUpdate.size)
                    postDao.update(postsToUpdate.map { it.copy(syncState = SyncState.UP_TO_DATE) })
            }
        }

    /**
     * Delete
     */

    /**
     * Sends a posts delete request to the api for the locally deleted posts
     *
     * @return A success or failure result
     */
    suspend fun sendDeletePosts(): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val postsToRemove = postDao.getPostsBySyncState(SyncState.PENDING_REMOVAL)
            if (postsToRemove.isNotEmpty()) {
                val request: List<DeletePostRequest> = postsToRemove.map { DeletePostMapper.toNetworkRequest(it) }
                postApiService.deletePosts(authHeader, request)
                postDao.deletePosts(postsToRemove)
            }
        }
}
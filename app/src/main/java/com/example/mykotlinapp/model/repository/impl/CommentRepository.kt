package com.example.mykotlinapp.model.repository.impl

import android.content.Context
import com.example.mykotlinapp.di.Qualifiers
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.CommentDao
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.inputs.form.comment.CreateCommentInput
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentInput
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentVoteInput
import com.example.mykotlinapp.model.dto.ui.post.CommentDTO
import com.example.mykotlinapp.model.mappers.impl.comment.CommentMapper
import com.example.mykotlinapp.model.mappers.impl.comment.create.CreateCommentMapper
import com.example.mykotlinapp.model.mappers.impl.comment.delete.DeleteCommentMapper
import com.example.mykotlinapp.model.mappers.impl.comment.update.UpdateCommentMapper
import com.example.mykotlinapp.model.mappers.impl.comment.update.UpdateCommentVoteMapper
import com.example.mykotlinapp.model.repository.AppRepository
import com.example.mykotlinapp.network.dto.requests.comment.CreateCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.DeleteCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentVoteRequest
import com.example.mykotlinapp.network.dto.responses.CreateOperationResponse
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.post.CommentResponse
import com.example.mykotlinapp.network.service.CommentApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommentRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @Qualifiers.IoDispatcher private val dispatcher: CoroutineDispatcher,
    sharedPreferenceDao: SharedPreferenceDao,
    private val commentApiService: CommentApiService,
    private val commentDao: CommentDao,
) : AppRepository(sharedPreferenceDao) {

    /**
     * Retrieve local data
     */

    /**
     * Gets the list of comments to display for a given post
     *
     * @param postRemoteId The remote id of the post to get comments from
     * @return A flow containing the list of comments
     */
    fun getUserPostComments(postRemoteId: String): Flow<List<CommentDTO>> =
        commentDao.getUserCommentsFlow(postRemoteId).toDTO((CommentMapper::toDTO)(context))

    /**
     * Database
     */

    /**
     * Updates a local comment
     *
     * @param updateCommentInput The update comment form input
     */
    suspend fun updateComment(updateCommentInput: UpdateCommentInput): Result<Unit> =
        executeAction(dispatcher) {
            commentDao
                .getUserComment(updateCommentInput.remoteId)
                ?.let(UpdateCommentMapper.toLocalUpdate(updateCommentInput))
                ?.let { commentDao.update(it) }
        }

    /**
     * Updates a local comment vote state
     *
     * @param voteInput The comment vote input
     */
    suspend fun updateCommentVoteState(voteInput: UpdateCommentVoteInput): Result<Unit> =
        executeAction(dispatcher) {
            commentDao.getUserComment(voteInput.remoteId)
                ?.let(UpdateCommentVoteMapper.toLocalUpdate(voteInput))
                ?.let { commentDao.update(it) }
        }

    /**
     * Submits a comment for later deletion through an API request
     *
     * @param commentRemoteId The remote id of the comment to submit for deletion
     */
    suspend fun submitCommentForDeletion(commentRemoteId: String): Result<Unit> =
        executeAction(dispatcher) {
            commentDao.getUserComment(commentRemoteId)?.let {
                commentDao.update(it.copy(syncState = SyncState.PENDING_REMOVAL))
            }
        }

    /**
     * Updates local comments with the response received from the API
     *
     * @param commentResponseList The API response
     */
    private suspend fun updateCommentsFromResponse(commentResponseList: List<CommentResponse>) =
        updateData(
            CommentMapper.toEntity(commentResponseList),
            DataBulkOperations(
                commentDao::getUserCommentsByIds,
                commentDao::insertUserComments,
                commentDao::clearCommentsNotIn
            )
        )

    /**
     * Network
     */

    /**
     * Create
     */

    /**
     * Sends a comment creation request to the API from the creation form input
     *
     * @param groupRemoteId The remote id of the group to create the comment in
     * @param postRemoteId The remote id of the post to create the comment for
     * @param createCommentInput The comment creation form input
     * @return The newly created comment remote id
     */
    suspend fun sendCreateComment(groupRemoteId: String, postRemoteId: String, createCommentInput: CreateCommentInput): Result<String> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val request: CreateCommentRequest = CreateCommentMapper.toNetworkRequest(createCommentInput)
            val response: CreateOperationResponse = commentApiService.createComment(authHeader, groupRemoteId, postRemoteId, request)
            response.remoteId
        }

    /**
     * Read
     */

    /**
     * Retrieves the comment list from the API and saves them to the local database
     *
     * @param groupRemoteId The remote id of the group to retrieve the comments from
     * @param postRemoteId The remote id of the post to retrieve the comments for
     * @return A success or failure result
     */
    suspend fun retrieveCommentList(groupRemoteId: String, postRemoteId: String): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val commentListResponse: List<CommentResponse> = commentApiService.getCommentList(authHeader, groupRemoteId, postRemoteId)
            updateCommentsFromResponse(commentListResponse)
        }

    /**
     * Update
     */

    /**
     * Sends an update requests to the API for the locally updated comments
     *
     * @return A success or failure result
     */
    suspend fun sendUpdateComments(): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val commentsToUpdate = commentDao.getUserCommentsBySyncState(SyncState.PENDING_UPDATE)
            if (commentsToUpdate.isNotEmpty()) {
                val request: List<UpdateCommentRequest> = commentsToUpdate.map(UpdateCommentMapper::toNetworkRequest)
                val response: UpdateOperationResponse =
                    commentApiService.updateComments(authHeader, request)
                if (response.modified == commentsToUpdate.size)
                    commentDao.update(commentsToUpdate.map { it.copy(syncState = SyncState.UP_TO_DATE) })
            }
        }

    /**
     * Sends an update request to the API for the locally updated comment votes
     *
     * @return A success or failure result
     */
    suspend fun sendUpdateCommentVoteStates(): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val commentsToUpdate = commentDao.getUserCommentsBySyncState(SyncState.PENDING_UPDATE)
            if (commentsToUpdate.isNotEmpty()) {
                val request: List<UpdateCommentVoteRequest> = commentsToUpdate.map(UpdateCommentVoteMapper::toNetworkRequest)
                val response: UpdateOperationResponse =
                    commentApiService.updateCommentVotes(authHeader, request)
                if (response.modified == commentsToUpdate.size)
                    commentDao.update(commentsToUpdate.map { it.copy(syncState = SyncState.UP_TO_DATE) })
            }
        }

    /**
     * Delete
     */

    /**
     * Sends a comments delete request to the api for the locally deleted comments
     *
     * @return A success or failure result
     */
    suspend fun sendDeleteComments(): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val commentsToRemove = commentDao.getUserCommentsBySyncState(SyncState.PENDING_REMOVAL)
            if (commentsToRemove.isNotEmpty()) {
                val request: List<DeleteCommentRequest> = commentsToRemove.map { DeleteCommentMapper.toNetworkRequest(it) }
                commentApiService.deleteComments(authHeader, request)
                commentDao.deleteComments(commentsToRemove)
            }
        }
}
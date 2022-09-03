package com.example.mykotlinapp.model.repository.impl

import android.content.Context
import com.example.mykotlinapp.di.Qualifiers.IoDispatcher
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.ChatDao
import com.example.mykotlinapp.model.dao.GroupDao
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateChatInput
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateGroupInput
import com.example.mykotlinapp.model.dto.ui.group.GroupDTO
import com.example.mykotlinapp.model.dto.ui.post.PostGroupData
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.mappers.impl.chat.update.UpdateChatMapper
import com.example.mykotlinapp.model.mappers.impl.group.GroupMapper
import com.example.mykotlinapp.model.mappers.impl.group.update.UpdateGroupMapper
import com.example.mykotlinapp.model.repository.AppRepository
import com.example.mykotlinapp.network.dto.requests.chat.UpdateGroupRequest
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.service.GroupApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    sharedPreferenceDao: SharedPreferenceDao,
    private val groupApiService: GroupApiService,
    private val groupDao: GroupDao,
    private val chatDao: ChatDao,
) : AppRepository(sharedPreferenceDao) {

    /**
     * Retrieve from local database
     */

    /**
     * Gets a group property to display
     *
     * @param groupRemoteId The remote id of the group
     * @return A flow containing the retrieved group to display
     */
    fun getGroup(groupRemoteId: String): Flow<GroupDTO?> =
        groupDao
            .getGroupFlow(groupRemoteId)
            .distinctUntilChanged()
            .map { it?.let((GroupMapper::toDTO)(context)) }

    /**
     * Gets the group data to display in the app bar of a user post
     *
     * @param groupRemoteId The remote id of the group to get the data for
     * @return A flow containing the data for the post app bar
     */
    fun getUserPostAppBar(groupRemoteId: String): Flow<PostGroupData?> =
        groupDao
            .getGroupFlow(groupRemoteId)
            .distinctUntilChanged()
            .map { it?.let((GroupMapper::toPostGroupData)(context)) }

    /**
     * Get the amount of unread posts for a given group to display
     *
     * @param groupRemoteId The remote id of the group to get the count for
     * @return A flow containing the count of unread posts
     */
    fun getUnreadPostsCount(groupRemoteId: String): Flow<Int> = groupDao.getUnreadPostsCountFlow(groupRemoteId).distinctUntilChanged()

    /**
     * @param remoteId The id of the group
     * @return Whether the user is admin of a given group
     */
    suspend fun isGroupAdmin(remoteId: String): Boolean = groupDao.isGroupAdmin(remoteId) ?: false

    /**
     * Database
     */

    /**
     * Updates a local group
     *
     * @param updateGroupInput The update group form input
     */
    suspend fun updateGroup(updateGroupInput: UpdateGroupInput): Result<Unit> =
        executeAction(dispatcher) {
            groupDao.getGroup(updateGroupInput.remoteId)?.let { group ->
                groupDao.update(UpdateGroupMapper.toLocalUpdate(updateGroupInput)(group))
                updateGroupInput.name?.let { inputName ->
                    val updateChatInput = UpdateChatInput(inputName)
                    chatDao.getChat(group.chatRemoteId)
                        ?.let(UpdateChatMapper.toLocalUpdate(updateChatInput))
                        ?.let { chatDao.update(it) }
                }
            }
        }

    /**
     * Network
     */

    /**
     * Sends a read action to a given group to the API and reads it on the local database
     *
     * @param groupRemoteId The remote id of the group to send read action for
     * @return A success or failure result
     */
    suspend fun sendReadGroup(groupRemoteId: String): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val (remoteId: String, readTime: Long) = groupApiService.readGroup(authHeader, groupRemoteId)
            groupDao.readGroup(remoteId, readTime)
        }

    /**
     * Sends an update requests to the API for the locally updated groups
     *
     * @return A success or failure result
     */
    suspend fun sendUpdateGroupChats(): Result<Unit> =
        executeAuthenticatedAction(dispatcher) { authHeader ->
            val groupsToUpdate: List<GroupProperty> = groupDao.getGroupsBySyncState(SyncState.PENDING_UPDATE)
            if (groupsToUpdate.isNotEmpty()) {
                val request: List<UpdateGroupRequest> = groupsToUpdate.map(UpdateGroupMapper::toNetworkRequest)
                val response: UpdateOperationResponse = groupApiService.updateGroups(authHeader, request)
                if (response.modified == groupsToUpdate.size) {
                    val chatIdsToUpdate: List<String> = chatDao.getChatIdsBySyncState(SyncState.PENDING_UPDATE)
                    groupDao.update(groupsToUpdate.map { it.copy(syncState = SyncState.UP_TO_DATE) })
                    chatDao.updateChatSyncStateByIds(chatIdsToUpdate, SyncState.UP_TO_DATE)
                }
            }
        }
}
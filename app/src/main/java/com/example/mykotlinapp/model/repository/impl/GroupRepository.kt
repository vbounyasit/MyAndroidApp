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
import com.example.mykotlinapp.model.entity.chat.ChatProperty
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.mappers.impl.chat.update.UpdateChatMapper
import com.example.mykotlinapp.model.mappers.impl.group.GroupMapper
import com.example.mykotlinapp.model.mappers.impl.group.update.UpdateGroupMapper
import com.example.mykotlinapp.network.dto.requests.chat.UpdateGroupRequest
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.chat.ReadGroupResponse
import com.example.mykotlinapp.network.service.GroupApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val sharedPreferenceDao: SharedPreferenceDao,
    private val groupApiService: GroupApiService,
    private val groupDao: GroupDao,
    private val chatDao: ChatDao,
) {

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
        groupDao.getGroupFlow(groupRemoteId).distinctUntilChanged()
            .map { it?.let((GroupMapper::toDTO)(context)) }

    /**
     * Gets the group data to display in the app bar of a user post
     *
     * @param groupRemoteId The remote id of the group to get the data for
     * @return A flow containing the data for the post app bar
     */
    fun getUserPostAppBar(groupRemoteId: String): Flow<PostGroupData?> =
        groupDao.getGroupFlow(groupRemoteId).distinctUntilChanged()
            .map { it?.let((GroupMapper::toPostGroupData)(context)) }

    /**
     * Get the amount of unread posts for a given group to display
     *
     * @param groupRemoteId The remote id of the group to get the count for
     * @return A flow containing the count of unread posts
     */
    fun getUnreadPostsCount(groupRemoteId: String): Flow<Int> =
        groupDao.getUnreadPostsCountFlow(groupRemoteId).distinctUntilChanged()

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
    suspend fun updateGroup(updateGroupInput: UpdateGroupInput) {
        withContext(dispatcher) {
            val group = groupDao.getGroup(updateGroupInput.remoteId)
            group?.let { foundGroup ->
                updateGroupInput.name?.let { inputName ->
                    val chat = chatDao.getChat(foundGroup.chatRemoteId)
                    chat?.let { foundChat ->
                        chatDao.update(
                            UpdateChatMapper.toLocalUpdate(
                                UpdateChatInput(
                                    inputName
                                )
                            )(foundChat)
                        )
                    }
                }
                groupDao.update(
                    UpdateGroupMapper.toLocalUpdate(updateGroupInput)(
                        foundGroup
                    )
                )
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
    suspend fun sendReadGroup(groupRemoteId: String): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val readGroupResponse: ReadGroupResponse =
                    groupApiService.readGroup(authHeader, groupRemoteId)
                groupDao.readGroup(
                    readGroupResponse.groupRemoteId,
                    readGroupResponse.lastGroupReadTime
                )
            }
        }
    }

    /**
     * Sends an update requests to the API for the locally updated groups
     *
     * @return A success or failure result
     */
    suspend fun sendUpdateGroups(): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val groupsToUpdate: List<GroupProperty> =
                    groupDao.getGroupsBySyncState(SyncState.PENDING_UPDATE)
                val chatsToUpdate: List<ChatProperty> =
                    chatDao.getChatsBySyncState(SyncState.PENDING_UPDATE)
                if (groupsToUpdate.isNotEmpty()) {
                    val request: List<UpdateGroupRequest> =
                        groupsToUpdate.map { UpdateGroupMapper.toNetworkRequest(it) }
                    val response: UpdateOperationResponse =
                        groupApiService.updateGroups(authHeader, request)
                    if (response.modified == groupsToUpdate.size) {
                        groupDao.update(groupsToUpdate.map { it.copy(syncState = SyncState.UP_TO_DATE) })
                        chatDao.update(chatsToUpdate.map { it.copy(syncState = SyncState.UP_TO_DATE) })
                    }
                }
            }
        }
    }
}
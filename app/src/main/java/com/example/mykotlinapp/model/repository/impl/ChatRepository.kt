package com.example.mykotlinapp.model.repository.impl

import android.content.Context
import com.example.mykotlinapp.di.Qualifiers.IoDispatcher
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.ChatDao
import com.example.mykotlinapp.model.dao.GroupDao
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatInput
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatLogInput
import com.example.mykotlinapp.model.dto.ui.chat.ChatBubbleDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatListItemDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatParticipantDTO
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.entity.chat.ChatItem
import com.example.mykotlinapp.model.entity.chat.ChatLog
import com.example.mykotlinapp.model.mappers.impl.chat.*
import com.example.mykotlinapp.model.mappers.impl.chat.ChatParticipantMapper.ChatContactParticipantMapper
import com.example.mykotlinapp.model.mappers.impl.chat.create.CreateChatLogMapper.toNetworkRequest
import com.example.mykotlinapp.model.mappers.impl.chat.create.CreateChatMapper
import com.example.mykotlinapp.model.mappers.impl.chat.create.PendingChatLogMapper
import com.example.mykotlinapp.model.mappers.impl.group.GroupMapper
import com.example.mykotlinapp.model.mappers.impl.user.UserContactMapper
import com.example.mykotlinapp.network.dto.requests.chat.CreateChatLogRequest
import com.example.mykotlinapp.network.dto.requests.chat.CreateChatRequest
import com.example.mykotlinapp.network.dto.responses.CreateOperationResponse
import com.example.mykotlinapp.network.dto.responses.chat.*
import com.example.mykotlinapp.network.service.ChatApiService
import com.example.mykotlinapp.network.socket.dto.ChatReadMessage
import com.example.mykotlinapp.ui.components.notifications.NotificationComponent.ChatLogsAppNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val sharedPreferenceDao: SharedPreferenceDao,
    private val chatApiService: ChatApiService,
    private val chatDao: ChatDao,
    private val groupDao: GroupDao,
) {

    /**
     * Retrieve from local database
     */

    /**
     * Gets the list of chat items to display
     * @return A flow containing the list of chat items
     */
    fun getChatItems(): Flow<List<ChatListItemDTO>> =
        chatDao.getChatItemsFlow().map { it.toList().map((ChatListItemMapper::toDTO)(context)) }

    /**
     * Gets the chat data for a given chat remote id
     *
     * @param chatRemoteId The remote id of the chat to get the data for
     * @return The chat property
     */
    suspend fun getChat(chatRemoteId: String): ChatDTO? =
        chatDao.getChat(chatRemoteId)?.let((ChatPropertyMapper::toDTO)(context))

    /**
     * Gets a chat property data to display
     *
     * @param chatRemoteId The remote id of the chat to get data for
     * @return A flow containing a chat property to display
     */
    fun getChatFlow(chatRemoteId: String): Flow<ChatDTO?> =
        chatDao.getChatFlow(chatRemoteId).distinctUntilChanged()
            .map { it?.let((ChatPropertyMapper::toDTO)(context)) }

    /**
     * Get chat participants for a given chat to display
     *
     * @param chatRemoteId The remote id of the chat to get participants from
     * @return A flow containing the list of chat participants
     */
    fun getChatParticipants(chatRemoteId: String): Flow<List<ChatParticipantDTO>> =
        chatDao.getChatParticipantsFlow(chatRemoteId)
            .map { participants -> participants.map { ChatParticipantMapper.toDTO(it) } }

    /**
     * Get chat bubbles for a given chat to display
     *
     * @param chatRemoteId The remote id of the chat to get the chat bubbles for
     * @return A flow containing the chat logs & notifications required
     */
    fun getChatBubbles(chatRemoteId: String): Flow<List<ChatBubbleDTO>> {
        val pendingChatLogsFlow =
            chatDao.getPendingChatLogsFlow(chatRemoteId)
                .map { it.map((PendingChatLogMapper::toDTO)(context)) }
        val chatLogsFlow =
            chatDao.getChatLogsFlow(chatRemoteId)
                .map { it.toList().map((ChatLogMapper::toDTO)(context)) }
        val chatNotificationsFlow =
            chatDao.getChatNotificationsFlow(chatRemoteId)
                .map { it.map(ChatNotificationMapper::toDTO) }
        return chatLogsFlow
            .combine(chatNotificationsFlow) { x, y -> x + y }
            .combine(pendingChatLogsFlow) { x, y -> x + y }
            .map { it.sortedBy(ChatBubbleDTO::creationTimeStamp) }
    }

    /**
     * Creates a notification out of the newly received chat logs alongside the chat history of unread messages
     *
     * @param logRemoteIds The newly received chat logs remote id
     * @return The list of chat log notifications
     */
    suspend fun getChatLogsAppNotifications(logRemoteIds: List<String>): List<ChatLogsAppNotification>? {
        return withContext(dispatcher) {
            sharedPreferenceDao.getAuthUserRemoteId()?.let { userRemoteId ->
                val newLogs = chatDao.getChatLogs(logRemoteIds)
                val chatRemoteIds = newLogs.map(ChatLog::chatRemoteId).distinct()
                val chatLogsWithChat: Map<ChatLog, ChatItem> =
                    chatDao.getUnreadChatLogs(chatRemoteIds)
                chatLogsWithChat.toList().groupBy { it.second }
                    .map { (chatItem, value) ->
                        val chatItemDTO = ChatItemMapper.toDTO(context)(chatItem)
                        val chatLogs = value.map { ChatLogMapper.toDTO(context)(it.first) }
                        ChatLogsAppNotification(
                            userRemoteId,
                            chatItemDTO,
                            chatItemDTO.isGroupChat,
                            chatLogs
                        )
                    }
            }
        }
    }

    /**
     * Gets the amount of unread chat logs for a given chat to display
     *
     * @param chatRemoteId The remote id of the chat to get the count for
     * @return A flow containing the count of unread chat logs
     */
    fun getUnreadChatLogsCount(chatRemoteId: String): Flow<Int> =
        chatDao.getUnreadChatLogsCountFlow(chatRemoteId).distinctUntilChanged()

    /**
     * Gets the participants for a chat with the contact menu allowing the user to send contact requests to display
     *
     * @param chatRemoteId The remote id of the chat to get the contact participants for
     * @return A flow containing the list of participants
     */
    fun getContactParticipants(chatRemoteId: String): Flow<List<UserContactDTO>> =
        chatDao.getContactParticipantsFlow(chatRemoteId).map {
            it.map { (participant, contact) ->
                contact?.let((UserContactMapper::toDTO)(context))
                    ?: ChatContactParticipantMapper.toDTO(participant)
            }
        }


    /**
     * Database
     */

    /**
     * Inserts a pending chat log creation request into the local database, to be sent to the API later on
     *
     * @param createChatLogInput The Chat log creation input from the form (message bar)
     */
    suspend fun createChatLog(createChatLogInput: CreateChatLogInput) {
        withContext(dispatcher) {
            chatDao.insert(PendingChatLogMapper.toEntity(createChatLogInput))
        }
    }

    /**
     * Updates a local chat participant read time
     *
     * @param chatReadMessage The chat read message object received from the server Socket connection
     */
    suspend fun updateChatParticipantReadTime(chatReadMessage: ChatReadMessage) {
        withContext(dispatcher) {
            chatDao.getChatParticipant(
                chatReadMessage.participantRemoteId,
                chatReadMessage.chatRemoteId
            )?.let {
                chatDao.update(it.copy(lastReadTime = chatReadMessage.readTime))
            }
        }
    }

    /**
     * Updates local chat items with the response received from the API
     *
     * @param chatListResponse The API response
     */
    private suspend fun updateChatItemsFromResponse(chatListResponse: List<ChatItemResponse>) {
        val pendingChats = chatDao.getPendingChats()
        val newChats =
            chatListResponse.asSequence().filter { !pendingChats.contains(it.chat.remoteId) }
                .map { ChatItemMapper.toEntity(it) }.toList()
        chatDao.clearChatItems(chatListResponse.map { it.chat.remoteId })
        chatDao.insertChatItems(newChats)
    }

    /**
     * Updates a local chat property from the API response
     *
     * @param chatResponse The API response
     */
    private suspend fun updateChatFromResponse(chatResponse: ChatResponse) {
        val chat = chatDao.getChat(chatResponse.chat.remoteId)
        if (chat == null || chat.syncState == SyncState.UP_TO_DATE)
            chatDao.insert(ChatPropertyMapper.toEntity(chatResponse))
    }

    /**
     * Updates local chat logs with the response received from the API
     *
     * @param chatLogsResponse The API response
     */
    private suspend fun updateChatLogsFromResponse(chatLogsResponse: List<ChatLogResponse>) {
        val pendingChatLogs = chatDao.getPendingChatLogs()
        val newChatLogs =
            chatLogsResponse.asSequence().filter { !pendingChatLogs.contains(it.remoteId) }
                .map { ChatLogMapper.toEntity(it) }.toList()
        chatDao.clearChatLogs(chatLogsResponse.map { it.remoteId })
        chatDao.insertChatLogs(newChatLogs)
    }

    private suspend fun updateNewChatLogsFromResponse(chatLogsResponse: List<ChatLogResponse>) {
        val newChatLogs = chatLogsResponse.map { ChatLogMapper.toEntity(it) }
        chatDao.insertChatLogs(newChatLogs)
    }

    /**
     * Updates local chat notifications with the response received from the API
     *
     * @param chatNotificationsResponse The API response
     * @param new Whether the notifications should only be added and not replace the local data
     */
    private suspend fun updateChatNotificationsFromResponse(
        chatNotificationsResponse: List<ChatNotificationResponse>,
        new: Boolean = false
    ) {
        val pendingChatNotifications = chatDao.getPendingChatNotifications()
        val newChatNotifications = chatNotificationsResponse.asSequence()
            .filter { !pendingChatNotifications.contains(it.remoteId) }
            .map { ChatNotificationMapper.toEntity(it) }.toList()
        if (!new) chatDao.clearChatNotifications(chatNotificationsResponse.map { it.remoteId })
        chatDao.insertChatNotifications(newChatNotifications)
    }

    /**
     * Updates a local chat group property with the response received from the API
     *
     * @param chatResponse The API response
     */
    private suspend fun updateChatGroupFromResponse(chatResponse: ChatResponse) {
        val group = groupDao.getGroup(chatResponse.group.remoteId)
        if (group == null || group.syncState == SyncState.UP_TO_DATE)
            groupDao.insert(GroupMapper.toEntity(chatResponse))
    }

    /**
     * Updates local chat participants with the response received from the API
     *
     * @param chatResponse The API response
     */
    private suspend fun updateChatParticipantsFromResponse(chatResponse: ChatResponse) {
        chatDao.clearChatParticipants(chatResponse.chat.remoteId)
        chatDao.insertParticipants(ChatParticipantMapper.toEntity(chatResponse))
    }

    /**
     * Network
     */

    /**
     * Create
     */

    /**
     * Sends a chat creation request to the API from the creation form input
     *
     * @param createChatInput The chat creation form input
     * @return The newly created chat remote id
     */
    suspend fun createChat(createChatInput: CreateChatInput): Result<String> {
        return withContext(dispatcher) {
            sharedPreferenceDao.getAPIAuthenticatedResult { authHeader ->
                val request: CreateChatRequest = CreateChatMapper.toNetworkRequest(createChatInput)
                val response: CreateOperationResponse =
                    chatApiService.createChat(authHeader, request)
                response.remoteId
            }
        }
    }

    /**
     * Sends the chat logs creation requests from the locally saved pending chat creation requests
     *
     * @return A success or failure result
     */
    suspend fun sendCreateChatLogs(): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val chatLogsToCreate = chatDao.getPendingChatLogsCreations()
                if (chatLogsToCreate.isNotEmpty()) {
                    val request: List<CreateChatLogRequest> =
                        chatLogsToCreate.map { toNetworkRequest(it) }
                    chatApiService.createChatLogs(authHeader, request)
                    chatDao.clearPendingChatLogsCreation()
                }
            }
        }
    }

    /**
     * Read
     */

    /**
     * Retrieves the chat items from the API and saves them to the local database
     *
     * @return A success or failure result
     */
    suspend fun retrieveChatItems(): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val chatListResponse: List<ChatItemResponse> =
                    chatApiService.getChatItems(authHeader)
                updateNewChatLogsFromResponse(chatListResponse.map { it.latestChatLog })
                updateChatItemsFromResponse(chatListResponse)
            }
        }
    }

    /**
     * Check for existence of the given chat items in local database, and retrieve the missing ones
     *
     * @param remoteIds The list of chat item remote ids to check for
     */
    private suspend fun retrieveMissingChatItems(remoteIds: List<String>) {
        return withContext(dispatcher) {
            val existingChatItems: List<String> = chatDao.getExistingChatItemIds(remoteIds)
            val missingChatItemIds = remoteIds.filterNot { existingChatItems.contains(it) }
            if (missingChatItemIds.isNotEmpty()) {
                sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                    val chatListResponse: List<ChatItemResponse> =
                        chatApiService.getChatItems(authHeader, missingChatItemIds)
                    updateChatItemsFromResponse(chatListResponse)
                }
            }
        }
    }

    /**
     * Retrieves a chat property from the API and saves them to the local database
     *
     * @param chatRemoteId The remote id of the chat to retrieve data for
     * @return A success or failure result
     */
    suspend fun retrieveChatData(chatRemoteId: String): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val chatResponse: ChatResponse =
                    chatApiService.getChatData(authHeader, chatRemoteId)
                updateChatFromResponse(chatResponse)
                updateChatParticipantsFromResponse(chatResponse)
                updateChatGroupFromResponse(chatResponse)
            }
        }
    }

    /**
     * Retrieves the chat bubbles for a given chat from the API and saves them to the local database
     *
     * @param chatRemoteId The remote id of the chat to retrieve chat logs for
     * @return A success or failure result
     */
    suspend fun retrieveChatBubbles(chatRemoteId: String): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val chatBubblesResponse: ChatBubblesResponse =
                    chatApiService.getChatBubbles(authHeader, chatRemoteId)
                updateChatLogsFromResponse(chatBubblesResponse.logs)
                updateChatNotificationsFromResponse(chatBubblesResponse.notifications)
            }
        }
    }

    /**
     * Retrieves a list of chat logs with the given remote ids from the API and saves them to the local database
     *
     * @param chatLogRemoteIds The remote ids of the chat logs to retrieve
     * @return A success or failure result
     */
    suspend fun retrieveNewChatLogs(chatLogRemoteIds: List<String>): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val response: List<ChatLogResponse> =
                    chatApiService.getNewChatLogs(authHeader, chatLogRemoteIds)
                val chatRemoteIds =
                    response.asSequence().map { it.chatRemoteId }.distinct().toList()
                updateNewChatLogsFromResponse(response)
                retrieveMissingChatItems(chatRemoteIds)
            }
        }
    }

    /**
     * Retrieves a list of chat notifications with the given remote ids from the API and saves them to the local database
     *
     * @param chatNotificationRemoteIds The remote id of the chat to retrieve chat notifications for
     * @return A success or failure result
     */
    suspend fun retrieveNewChatNotifications(chatNotificationRemoteIds: List<String>): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val response: List<ChatNotificationResponse> =
                    chatApiService.getNewChatNotifications(authHeader, chatNotificationRemoteIds)
                updateChatNotificationsFromResponse(response, true)
            }
        }
    }

    /**
     * Update
     */

    /**
     * Notify the remote server that the authenticated user has read a given chat conversation
     * and update the local data accordingly
     *
     * @param chatRemoteId The remote id of the chat to read
     * @return A success or failure result
     */
    suspend fun sendReadChat(chatRemoteId: String): Result<Unit> {
        return withContext(dispatcher) {
            sharedPreferenceDao.performAPIAuthenticatedAction { authHeader ->
                val readChatResponse: ReadChatResponse =
                    chatApiService.readChat(authHeader, chatRemoteId)
                chatDao.updateReadChat(readChatResponse.chatRemoteId, readChatResponse.lastReadTime)
                chatDao.updateReadChatItem(
                    readChatResponse.chatRemoteId,
                    readChatResponse.lastReadTime
                )
            }
        }
    }

    //TODO remove chat AKA leave chat + participants update / add/remove

}
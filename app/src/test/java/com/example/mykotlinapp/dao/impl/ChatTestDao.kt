package com.example.mykotlinapp.dao.impl

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestLocalStorage
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.ChatDao
import com.example.mykotlinapp.model.entity.chat.*
import com.example.mykotlinapp.model.entity.pending.PendingChatLogCreation
import com.example.mykotlinapp.model.entity.user.UserContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatTestDao : ChatDao, TestDao {

    private val chatLocalStorage = TestLocalStorage<ChatProperty, String> { it.remoteId }
    private val chatItemsLocalStorage = TestLocalStorage<ChatItem, String> { it.remoteId }
    private val chatLogsLocalStorage = TestLocalStorage<ChatLog, String> { it.remoteId }
    private val chatNotificationsLocalStorage = TestLocalStorage<ChatNotification, String> { it.remoteId }
    private val pendingChatLogLocalStorage = TestLocalStorage<PendingChatLogCreation, Long> { it.id }
    private val chatParticipantsLocalStorage = TestLocalStorage<ChatParticipant, String> { it.remoteId }

    override suspend fun insert(chatProperty: ChatProperty) {
        chatLocalStorage.insert(chatProperty)
    }

    override suspend fun insert(pendingChatLogCreation: PendingChatLogCreation) {
        pendingChatLogLocalStorage.insert(pendingChatLogCreation)
    }

    override suspend fun insertParticipants(chatParticipants: List<ChatParticipant>) {
        chatParticipantsLocalStorage.insert(chatParticipants)
    }

    override suspend fun insertChatItems(chatItems: List<ChatItem>) {
        chatItemsLocalStorage.insert(chatItems)
    }

    override suspend fun insertChatLogs(chatLogs: List<ChatLog>) {
        chatLogsLocalStorage.insert(chatLogs)
    }

    override suspend fun insertChatNotifications(chatNotifications: List<ChatNotification>) {
        chatNotificationsLocalStorage.insert(chatNotifications)
    }

    override suspend fun getChat(remoteId: String): ChatProperty? = chatLocalStorage.get(remoteId)

    override suspend fun getChatItemsByIds(remoteIds: List<String>): List<ChatItem> = chatItemsLocalStorage.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getChatItemIdsIn(remoteIds: List<String>): List<String> = getChatItemsByIds(remoteIds).map(ChatItem::remoteId)

    override suspend fun getUnreadChatLogs(chatRemoteIds: List<String>): Map<ChatLog, ChatItem> = mapOf()//todo

    override suspend fun getChatLogsByIds(remoteIds: List<String>): List<ChatLog> = chatLogsLocalStorage.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getChatNotificationsByIds(remoteIds: List<String>): List<ChatNotification> = chatNotificationsLocalStorage.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getChatsBySyncState(syncState: SyncState): List<ChatProperty> = chatLocalStorage.getAllWhen { it.syncState == syncState }

    override suspend fun getChatParticipant(remoteId: String, chatRemoteId: String): ChatParticipant? =
        chatParticipantsLocalStorage.getWhen { it.remoteId == remoteId && it.chatRemoteId == chatRemoteId }

    override suspend fun getPendingChatLogsCreations(): List<PendingChatLogCreation> = pendingChatLogLocalStorage.getAll()

    override suspend fun update(chatProperty: ChatProperty) {
        chatLocalStorage.update(chatProperty)
    }

    override suspend fun update(chats: List<ChatProperty>) {
        chatLocalStorage.updateAll(chats)
    }

    override suspend fun update(chatParticipant: ChatParticipant) {
        chatParticipantsLocalStorage.update(chatParticipant)
    }

    override suspend fun updateReadChat(chatRemoteId: String, lastReadTime: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReadChatItem(chatRemoteId: String, lastReadTime: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun clearChatItemsNotIn(except: List<String>) {
        chatItemsLocalStorage.deleteWhen { !except.contains(it.remoteId) }
    }

    override suspend fun clearParticipantsByChat(chatRemoteId: String) {
        chatParticipantsLocalStorage.deleteWhen { it.remoteId == chatRemoteId }
    }

    override suspend fun clearChatLogsNotIn(except: List<String>) {
        chatLogsLocalStorage.deleteWhen { !except.contains(it.remoteId) }
    }

    override suspend fun clearChatNotificationsNotIn(except: List<String>) {
        chatNotificationsLocalStorage.deleteWhen { !except.contains(it.remoteId) }
    }

    override suspend fun clearPendingChatLogsCreation() {
        pendingChatLogLocalStorage.clear()
    }

    override fun getChatFlow(chatRemoteId: String): Flow<ChatProperty?> = flow {
        emit(getChat(chatRemoteId))
    }

    override fun getChatLogsFlow(chatRemoteId: String): Flow<List<ChatLog>> = flow {
        emit(chatLogsLocalStorage.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getChatNotificationsFlow(chatRemoteId: String): Flow<List<ChatNotification>> = flow {
        emit(chatNotificationsLocalStorage.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getPendingChatLogsFlow(chatRemoteId: String): Flow<List<PendingChatLogCreation>> = flow {
        emit(pendingChatLogLocalStorage.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getChatParticipantsFlow(chatRemoteId: String): Flow<List<ChatParticipant>> = flow {
        emit(chatParticipantsLocalStorage.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getUnreadChatLogsCountFlow(chatRemoteId: String): Flow<Int> = flow {
        TODO("Not yet implemented")
    }

    override fun getChatItemsFlow(): Flow<Map<ChatItem, ChatLog>> = flow {
        TODO("Not yet implemented")
    }

    override fun getContactParticipantsFlow(chatRemoteId: String): Flow<Map<ChatParticipant, UserContact?>> = flow {
        TODO("Not yet implemented")
    }

    override fun clear() {
        chatLocalStorage.clear()
        chatItemsLocalStorage.clear()
        chatLogsLocalStorage.clear()
        chatNotificationsLocalStorage.clear()
        chatParticipantsLocalStorage.clear()
        pendingChatLogLocalStorage.clear()
    }
}
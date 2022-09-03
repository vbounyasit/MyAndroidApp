package com.example.mykotlinapp.dao.impl

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestDataSource
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.ChatDao
import com.example.mykotlinapp.model.entity.chat.*
import com.example.mykotlinapp.model.entity.pending.PendingChatLogCreation
import com.example.mykotlinapp.model.entity.user.UserContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatTestDao : ChatDao, TestDao {

    private val chatTable = TestDataSource<String, ChatProperty> { it.remoteId }
    private val chatItemsTable = TestDataSource<String, ChatItem> { it.remoteId }
    private val chatLogsTable = TestDataSource<String, ChatLog> { it.remoteId }
    private val chatNotificationsTable = TestDataSource<String, ChatNotification> { it.remoteId }
    private val pendingChatLogTable = TestDataSource<Long, PendingChatLogCreation> { it.id }
    private val chatParticipantsTable = TestDataSource<String, ChatParticipant> { it.remoteId }

    private val userContactsTable = TestDataSource<String, UserContact> { it.remoteId }

    override suspend fun insert(chatProperty: ChatProperty) {
        chatTable.insert(chatProperty)
    }

    override suspend fun insert(pendingChatLogCreation: PendingChatLogCreation) {
        pendingChatLogTable.insert(pendingChatLogCreation)
    }

    override suspend fun insertParticipants(chatParticipants: List<ChatParticipant>) {
        chatParticipantsTable.insert(chatParticipants)
    }

    override suspend fun insertChatItems(chatItems: List<ChatItem>) {
        chatItemsTable.insert(chatItems)
    }

    override suspend fun insertChatLogs(chatLogs: List<ChatLog>) {
        chatLogsTable.insert(chatLogs)
    }

    override suspend fun insertChatNotifications(chatNotifications: List<ChatNotification>) {
        chatNotificationsTable.insert(chatNotifications)
    }

    override suspend fun getChat(remoteId: String): ChatProperty? = chatTable.get(remoteId)

    override suspend fun getChatItemsByIds(remoteIds: List<String>): List<ChatItem> = chatItemsTable.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getChatItemIdsIn(remoteIds: List<String>): List<String> = getChatItemsByIds(remoteIds).map(ChatItem::remoteId)

    override suspend fun getUnreadChatLogs(chatRemoteIds: List<String>): Map<ChatLog, ChatItem> =
        chatLogsTable.join(chatItemsTable) { chatLog, chatItem ->
            chatLog.chatRemoteId == chatItem.remoteId
                    && chatItem.lastReadTime?.let { readTime -> chatLog.creationTime > readTime } ?: false
        }

    override suspend fun getChatLogsByIds(remoteIds: List<String>): List<ChatLog> = chatLogsTable.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getUniqueChatLogIdsIn(remoteIds: List<String>): List<String> = getChatLogsByIds(remoteIds).map { it.remoteId }.distinct()

    override suspend fun getChatNotificationsByIds(remoteIds: List<String>): List<ChatNotification> = chatNotificationsTable.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getChatIdsBySyncState(syncState: SyncState): List<String> = chatTable.getAllWhen { it.syncState == syncState }.map { it.remoteId }

    override suspend fun getChatParticipant(remoteId: String, chatRemoteId: String): ChatParticipant? =
        chatParticipantsTable.getWhen { it.remoteId == remoteId && it.chatRemoteId == chatRemoteId }

    override suspend fun getPendingChatLogsCreations(): List<PendingChatLogCreation> = pendingChatLogTable.getAll()

    override suspend fun update(chatProperty: ChatProperty) = chatTable.update(chatProperty)

    override suspend fun update(chats: List<ChatProperty>) = chatTable.updateAll(chats)

    override suspend fun update(chatParticipant: ChatParticipant) = chatParticipantsTable.update(chatParticipant)

    override suspend fun updateChatSyncStateByIds(remoteIds: List<String>, syncState: SyncState) = chatTable.update(remoteIds) { it.copy(syncState = syncState) }

    override suspend fun updateParticipantReadTime(remoteId: String, chatRemoteId: String, readTime: Long) =
        chatParticipantsTable.update(remoteId) {
            if (it.chatRemoteId == chatRemoteId) it.copy(lastReadTime = readTime) else it
        }

    override suspend fun updateReadChat(chatRemoteId: String, lastReadTime: Long) =
        chatTable.update(chatRemoteId) { it.copy(lastReadTime = lastReadTime) }

    override suspend fun updateReadChatItem(chatRemoteId: String, lastReadTime: Long) =
        chatItemsTable.update(chatRemoteId) { it.copy(lastReadTime = lastReadTime) }

    override suspend fun clearChatItemsNotIn(except: List<String>) {
        chatItemsTable.deleteWhen { !except.contains(it.remoteId) }
    }

    override suspend fun clearParticipantsByChat(chatRemoteId: String) {
        chatParticipantsTable.deleteWhen { it.remoteId == chatRemoteId }
    }

    override suspend fun clearChatLogsNotIn(except: List<String>) {
        chatLogsTable.deleteWhen { !except.contains(it.remoteId) }
    }

    override suspend fun clearChatNotificationsNotIn(except: List<String>) {
        chatNotificationsTable.deleteWhen { !except.contains(it.remoteId) }
    }

    override suspend fun clearPendingChatLogsCreation() {
        pendingChatLogTable.clear()
    }

    override fun getChatFlow(chatRemoteId: String): Flow<ChatProperty?> = flow {
        emit(getChat(chatRemoteId))
    }

    override fun getChatLogsFlow(chatRemoteId: String): Flow<List<ChatLog>> = flow {
        emit(chatLogsTable.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getChatNotificationsFlow(chatRemoteId: String): Flow<List<ChatNotification>> = flow {
        emit(chatNotificationsTable.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getPendingChatLogsFlow(chatRemoteId: String): Flow<List<PendingChatLogCreation>> = flow {
        emit(pendingChatLogTable.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getChatParticipantsFlow(chatRemoteId: String): Flow<List<ChatParticipant>> = flow {
        emit(chatParticipantsTable.getAllWhen { it.chatRemoteId == chatRemoteId })
    }

    override fun getContactParticipantsFlow(chatRemoteId: String): Flow<Map<ChatParticipant, UserContact?>> = flow {
        emit(
            chatParticipantsTable.leftJoin(userContactsTable) { participant, contact ->
                participant.remoteId == contact.remoteId && participant.chatRemoteId == chatRemoteId
            }.mapValues { it.value.firstOrNull() }
        )
    }

    override fun getUnreadChatLogsCountFlow(chatRemoteId: String): Flow<Int> = flow {
        emit(
            chatLogsTable.join(chatTable) { chatLog, chat ->
                chatLog.chatRemoteId == chat.remoteId
                        && chatLog.chatRemoteId == chatRemoteId
                        && chat.lastReadTime?.let { chatLog.creationTime > it } ?: true
            }.size
        )
    }

    override fun getChatItemsFlow(): Flow<Map<ChatItem, ChatLog>> = flow {
        emit(
            chatItemsTable
                .leftJoin(chatLogsTable) { chatItem, chatLog -> chatItem.remoteId == chatLog.chatRemoteId }
                .mapNotNull { (chat, logs) -> logs.maxByOrNull { it.creationTime }?.let { Pair(chat, it) } }
                .toMap()
        )
    }

    override fun clear() {
        chatTable.clear()
        chatItemsTable.clear()
        chatLogsTable.clear()
        chatNotificationsTable.clear()
        chatParticipantsTable.clear()
        pendingChatLogTable.clear()
    }
}
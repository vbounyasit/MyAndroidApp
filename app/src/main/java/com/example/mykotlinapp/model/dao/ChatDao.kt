package com.example.mykotlinapp.model.dao

import androidx.room.*
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.chat.*
import com.example.mykotlinapp.model.entity.pending.PendingChatLogCreation
import com.example.mykotlinapp.model.entity.user.UserContact
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    /**
     * Create
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatProperty: ChatProperty)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(chatParticipants: List<ChatParticipant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatItems(chatItems: List<ChatItem>)

    @Insert
    suspend fun insert(pendingChatLogCreation: PendingChatLogCreation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatLogs(chatLogs: List<ChatLog>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatNotifications(chatNotifications: List<ChatNotification>)

    /**
     * Read
     */

    @Query("SELECT * from chats WHERE chat_remote_id = :remoteId")
    suspend fun getChat(remoteId: String): ChatProperty?

    @Query("SELECT * from chat_items WHERE chat_remote_id IN (:remoteIds)")
    suspend fun getChatItemsByIds(remoteIds: List<String>): List<ChatItem>

    @Query("SELECT chat_remote_id from chat_items WHERE chat_remote_id IN (:remoteIds)")
    suspend fun getChatItemIdsIn(remoteIds: List<String>): List<String>

    @Query("SELECT * FROM chat_logs JOIN chat_items ON chat_remote_id = log_chat_remote_id WHERE log_creation_time > chat_last_read AND chat_remote_id IN (:chatRemoteIds)")
    suspend fun getUnreadChatLogs(chatRemoteIds: List<String>): Map<ChatLog, ChatItem>

    @Query("SELECT * from chat_logs WHERE log_remote_id IN (:remoteIds)")
    suspend fun getChatLogsByIds(remoteIds: List<String>): List<ChatLog>

    @Query("SELECT DISTINCT log_remote_id from chat_logs WHERE log_remote_id IN (:remoteIds)")
    suspend fun getUniqueChatLogIdsIn(remoteIds: List<String>): List<String>

    @Query("SELECT * from chat_notifications WHERE remoteId IN (:remoteIds)")
    suspend fun getChatNotificationsByIds(remoteIds: List<String>): List<ChatNotification>

    @Query("SELECT chat_remote_id from chats WHERE sync_state = :syncState")
    suspend fun getChatIdsBySyncState(syncState: SyncState): List<String>

    @Query("SELECT * from chat_participants WHERE participant_remote_id = :remoteId AND participant_chat_remote_id = :chatRemoteId")
    suspend fun getChatParticipant(remoteId: String, chatRemoteId: String): ChatParticipant?

    @Query("SELECT * from pending_chat_logs_creation")
    suspend fun getPendingChatLogsCreations(): List<PendingChatLogCreation>

    /**
     * Update
     */

    @Update
    suspend fun update(chatProperty: ChatProperty)

    @Update
    suspend fun update(chats: List<ChatProperty>)

    @Update
    suspend fun update(chatParticipant: ChatParticipant)

    @Query("UPDATE chats SET sync_state = :syncState WHERE chat_remote_id IN (:remoteIds)")
    suspend fun updateChatSyncStateByIds(remoteIds: List<String>, syncState: SyncState)

    @Query("UPDATE chat_participants SET last_read_time = :readTime WHERE participant_remote_id = :remoteId AND participant_chat_remote_id = :chatRemoteId")
    suspend fun updateParticipantReadTime(remoteId: String, chatRemoteId: String, readTime: Long)

    @Query("UPDATE chats SET last_read = :lastReadTime WHERE chat_remote_id = :chatRemoteId")
    suspend fun updateReadChat(chatRemoteId: String, lastReadTime: Long)

    @Query("UPDATE chat_items SET chat_last_read = :lastReadTime WHERE chat_remote_id = :chatRemoteId")
    suspend fun updateReadChatItem(chatRemoteId: String, lastReadTime: Long)

    /**
     * Delete
     */

    @Query("DELETE from chat_items WHERE chat_remote_id NOT IN (:except)")
    suspend fun clearChatItemsNotIn(except: List<String>)

    @Query("DELETE from chat_participants WHERE participant_chat_remote_id = :chatRemoteId")
    suspend fun clearParticipantsByChat(chatRemoteId: String)

    @Query("DELETE from chat_logs WHERE log_remote_id NOT IN (:except)")
    suspend fun clearChatLogsNotIn(except: List<String>)

    @Query("DELETE from chat_notifications WHERE remoteId NOT IN (:except)")
    suspend fun clearChatNotificationsNotIn(except: List<String>)

    @Query("DELETE from pending_chat_logs_creation")
    suspend fun clearPendingChatLogsCreation()

    /**
     * Flows
     */

    @Query("SELECT * from chats WHERE chat_remote_id = :chatRemoteId")
    fun getChatFlow(chatRemoteId: String): Flow<ChatProperty?>

    @Query("SELECT * from chat_logs WHERE log_chat_remote_id = :chatRemoteId")
    fun getChatLogsFlow(chatRemoteId: String): Flow<List<ChatLog>>

    @Query("SELECT * from chat_notifications WHERE chat_remote_id = :chatRemoteId")
    fun getChatNotificationsFlow(chatRemoteId: String): Flow<List<ChatNotification>>

    @Query("SELECT * from pending_chat_logs_creation WHERE chat_remote_id = :chatRemoteId")
    fun getPendingChatLogsFlow(chatRemoteId: String): Flow<List<PendingChatLogCreation>>

    @Query("SELECT * from chat_participants WHERE participant_chat_remote_id = :chatRemoteId")
    fun getChatParticipantsFlow(chatRemoteId: String): Flow<List<ChatParticipant>>

    @Query("SELECT * FROM chat_participants LEFT JOIN user_contacts ON participant_remote_id = contact_remote_id WHERE participant_chat_remote_id = :chatRemoteId")
    fun getContactParticipantsFlow(chatRemoteId: String): Flow<Map<ChatParticipant, UserContact?>>

    @Query("SELECT COUNT(*) from chat_logs JOIN chats ON log_chat_remote_id = chat_remote_id WHERE log_chat_remote_id = :chatRemoteId AND (last_read IS null OR log_creation_time > last_read)")
    fun getUnreadChatLogsCountFlow(chatRemoteId: String): Flow<Int>

    @Query("SELECT * FROM chat_items JOIN chat_logs ON chat_remote_id = log_chat_remote_id GROUP BY chat_remote_id HAVING log_creation_time = MAX(log_creation_time)")
    fun getChatItemsFlow(): Flow<Map<ChatItem, ChatLog>>

}
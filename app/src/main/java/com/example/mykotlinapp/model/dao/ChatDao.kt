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

    @Query("SELECT last_read from chats WHERE chat_remote_id = :remoteId")
    suspend fun getChatReadTime(remoteId: String): Long?

    @Query("SELECT * from chat_items WHERE chat_remote_id = :remoteId")
    suspend fun getChatItem(remoteId: String): ChatItem?

    @Query("SELECT chat_remote_id from chat_items WHERE chat_remote_id IN (:remoteIds)")
    suspend fun getExistingChatItemIds(remoteIds: List<String>): List<String>

    @Query("SELECT COUNT(*) from chat_participants WHERE participant_chat_remote_id = :remoteId")
    suspend fun getChatParticipantsCount(remoteId: String): Int

    @Query("SELECT * FROM chat_logs JOIN chat_items ON chat_remote_id = log_chat_remote_id WHERE log_creation_date > chat_last_read AND chat_remote_id IN (:chatRemoteIds)")
    suspend fun getUnreadChatLogs(chatRemoteIds: List<String>): Map<ChatLog, ChatItem>

    @Query("SELECT * from chat_logs WHERE log_remote_id IN (:remoteIds)")
    suspend fun getChatLogs(remoteIds: List<String>): List<ChatLog>

    @Query("SELECT * from chats WHERE sync_state = :syncState")
    suspend fun getChatsBySyncState(syncState: SyncState): List<ChatProperty>

    @Query("SELECT * from chat_participants WHERE participant_remote_id = :remoteId AND participant_chat_remote_id = :chatRemoteId")
    suspend fun getChatParticipant(remoteId: String, chatRemoteId: String): ChatParticipant?

    @Query("SELECT chat_remote_id from chats WHERE sync_state != :syncState")
    suspend fun getPendingChats(syncState: SyncState = SyncState.UP_TO_DATE): List<String>

    @Query("SELECT * from pending_chat_logs_creation")
    suspend fun getPendingChatLogsCreations(): List<PendingChatLogCreation>

    @Query("SELECT log_remote_id from chat_logs WHERE log_sync_state != :syncState")
    suspend fun getPendingChatLogs(syncState: SyncState = SyncState.UP_TO_DATE): List<String>

    @Query("SELECT remoteId from chat_notifications WHERE sync_state != :syncState")
    suspend fun getPendingChatNotifications(syncState: SyncState = SyncState.UP_TO_DATE): List<String>

    /**
     * Update
     */

    @Update
    suspend fun update(chatProperty: ChatProperty)

    @Update
    suspend fun update(chats: List<ChatProperty>)

    @Update
    suspend fun update(chatParticipant: ChatParticipant)

    @Query("UPDATE chats SET last_read = :lastReadTime WHERE chat_remote_id = :chatRemoteId")
    suspend fun updateReadChat(chatRemoteId: String, lastReadTime: Long)

    @Query("UPDATE chat_items SET chat_last_read = :lastReadTime WHERE chat_remote_id = :chatRemoteId")
    suspend fun updateReadChatItem(chatRemoteId: String, lastReadTime: Long)

    /**
     * Delete
     */

    @Query("DELETE from chat_items WHERE chat_sync_state = :syncState AND chat_remote_id NOT IN (:except)")
    suspend fun clearChatItems(except: List<String>, syncState: SyncState = SyncState.UP_TO_DATE)

    @Query("DELETE from chat_participants WHERE participant_chat_remote_id = :chatRemoteId")
    suspend fun clearChatParticipants(chatRemoteId: String)

    @Query("DELETE from chat_logs WHERE log_sync_state = :syncState AND log_remote_id NOT IN (:except)")
    suspend fun clearChatLogs(except: List<String>, syncState: SyncState = SyncState.UP_TO_DATE)

    @Query("DELETE from chat_notifications WHERE sync_state = :syncState AND remoteId NOT IN (:except)")
    suspend fun clearChatNotifications(
        except: List<String>,
        syncState: SyncState = SyncState.UP_TO_DATE
    )

    @Query("DELETE from pending_chat_logs_creation")
    suspend fun clearPendingChatLogsCreation()

    /**
     * Flows
     */

    @Query("SELECT * from chats WHERE chat_remote_id = :chatRemoteId")
    fun getChatFlow(chatRemoteId: String): Flow<ChatProperty?>

    @Query("SELECT COUNT(*) from chat_logs JOIN chats ON log_chat_remote_id = chat_remote_id WHERE log_chat_remote_id = :chatRemoteId AND (last_read IS null OR log_creation_date > last_read)")
    fun getUnreadChatLogsCountFlow(chatRemoteId: String): Flow<Int>

    @Query("SELECT * FROM chat_items JOIN chat_logs ON chat_remote_id = log_chat_remote_id GROUP BY chat_remote_id HAVING log_creation_date = MAX(log_creation_date)")
    fun getChatItemsFlow(): Flow<Map<ChatItem, ChatLog>>

    @Query("SELECT * from chat_logs WHERE log_chat_remote_id = :chatRemoteId")
    fun getChatLogsFlow(chatRemoteId: String): Flow<List<ChatLog>>

    @Query("SELECT * from chat_participants WHERE participant_chat_remote_id = :chatRemoteId")
    fun getChatParticipantsFlow(chatRemoteId: String): Flow<List<ChatParticipant>>

    @Query("SELECT * FROM chat_participants LEFT JOIN user_contacts ON participant_remote_id = contact_remote_id WHERE participant_chat_remote_id = :chatRemoteId")
    fun getContactParticipantsFlow(chatRemoteId: String): Flow<Map<ChatParticipant, UserContact?>>

    @Query("SELECT * from pending_chat_logs_creation WHERE chat_remote_id = :chatRemoteId")
    fun getPendingChatLogsFlow(chatRemoteId: String): Flow<List<PendingChatLogCreation>>

    @Query("SELECT * from chat_notifications WHERE chat_remote_id = :chatRemoteId")
    fun getChatNotificationsFlow(chatRemoteId: String): Flow<List<ChatNotification>>
}
package com.example.mykotlinapp.model.dao

import androidx.room.*
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.entity.user.UserContact
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    /**
     * Create
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userContacts: List<UserContact>)

    /**
     * Read
     */

    @Query("SELECT * FROM users WHERE remote_id = :remoteId")
    suspend fun getUser(remoteId: String): User?

    @Query("SELECT * FROM users WHERE remote_id = :remoteId AND sync_state = :syncState")
    suspend fun getUserBySyncState(remoteId: String, syncState: SyncState): User?

    @Query("SELECT * FROM user_contacts WHERE contact_remote_id = :remoteId")
    suspend fun getContact(remoteId: String): UserContact?

    @Query("SELECT * from user_contacts WHERE contact_remote_id IN (:remoteIds)")
    suspend fun getContactsByIds(remoteIds: List<String>): List<UserContact>

    @Query("SELECT * from user_contacts WHERE contact_sync_state = :syncState")
    suspend fun getContactsBySyncState(syncState: SyncState): List<UserContact>

    /**
     * Update
     */

    @Update
    suspend fun update(user: User)

    @Update
    suspend fun update(userContact: UserContact)

    /**
     * Delete
     */
    @Query("DELETE FROM users WHERE remote_id = :remoteId")
    suspend fun deleteUser(remoteId: String)

    @Delete
    suspend fun deleteContacts(contacts: List<UserContact>)

    @Query("DELETE FROM user_contacts WHERE contact_remote_id NOT IN (:except)")
    suspend fun clearContactsNotIn(except: List<String>)

    /**
     * Flow
     */

    @Query("SELECT * FROM users WHERE remote_id = :remoteId")
    fun getUserFlow(remoteId: String): Flow<User?>

    @Query("SELECT * FROM user_contacts WHERE contact_relation_type = :relationType ORDER BY contact_full_name ASC")
    fun getContactsOfRelationFlow(relationType: ContactRelationType): Flow<List<UserContact>>

    @Query("SELECT * FROM user_contacts WHERE contact_relation_type != :relationType ORDER BY contact_full_name ASC")
    fun getContactsNotOfRelationFlow(relationType: ContactRelationType): Flow<List<UserContact>>


}
package com.example.mykotlinapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.group.GroupProperty
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    /**
     * Create
     */

    @Insert(onConflict = REPLACE)
    suspend fun insert(groupProperty: GroupProperty)

    /**
     * Read
     */

    @Query("SELECT * from groups WHERE group_remote_id = :remoteId")
    suspend fun getGroup(remoteId: String): GroupProperty?

    @Query("SELECT * from groups WHERE sync_state = :syncState")
    suspend fun getGroupsBySyncState(syncState: SyncState): List<GroupProperty>

    @Query("SELECT is_admin from groups WHERE group_remote_id = :remoteId")
    suspend fun isGroupAdmin(remoteId: String): Boolean?

    /**
     * Update
     */

    @Update
    suspend fun update(groupProperty: GroupProperty)

    @Update
    suspend fun update(groups: List<GroupProperty>)

    @Query("UPDATE groups SET last_read = :lastReadTime WHERE group_remote_id = :groupRemoteId")
    suspend fun readGroup(groupRemoteId: String, lastReadTime: Long)

    /**
     * Flow
     */

    @Query("SELECT * from groups WHERE group_remote_id = :remoteId")
    fun getGroupFlow(remoteId: String): Flow<GroupProperty?>

    @Query("SELECT COUNT(*) from user_posts JOIN groups ON parent_group_remote_id = group_remote_id WHERE group_remote_id = :groupRemoteId AND (last_read IS null OR post_time > last_read)")
    fun getUnreadPostsCountFlow(groupRemoteId: String): Flow<Int>

}
package com.example.mykotlinapp.model.dao

import androidx.room.*
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.post.UserComment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    /**
     * Create
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserComments(userComments: List<UserComment>)

    /**
     * Read
     */

    @Query("SELECT * from user_comments WHERE remote_id = :commentRemoteId")
    suspend fun getUserComment(commentRemoteId: String): UserComment?

    @Query("SELECT * from user_comments WHERE sync_state = :syncState")
    suspend fun getCommentsBySyncState(syncState: SyncState): List<UserComment>

    @Query("SELECT remote_id from user_comments WHERE sync_state != :syncState")
    suspend fun getPendingComments(syncState: SyncState = SyncState.UP_TO_DATE): List<String>

    /**
     * Update
     */

    @Update
    suspend fun update(comment: UserComment)

    /**
     * Delete
     */

    @Delete
    suspend fun deleteComments(comments: List<UserComment>)

    @Query("DELETE FROM user_comments WHERE sync_state = :syncState AND remote_id NOT IN (:except)")
    suspend fun clearComments(except: List<String>, syncState: SyncState = SyncState.UP_TO_DATE)

    /**
     * Flow
     */

    @Query("SELECT * from user_comments WHERE parent_post_remote_id = :postRemoteId ORDER BY order_index ASC")
    fun getUserCommentsFlow(postRemoteId: String): Flow<List<UserComment>>

}
package com.example.mykotlinapp.model.dao

import androidx.room.*
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.post.PostMedia
import com.example.mykotlinapp.model.entity.post.UserPost
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    /**
     * Create
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPosts: UserPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPosts(userPosts: List<UserPost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostMedias(userPostMedias: List<PostMedia>)

    /**
     * Read
     */

    @Query("SELECT * from user_posts WHERE post_remote_id = :postRemoteId")
    suspend fun getUserPost(postRemoteId: String): UserPost?

    @Query("SELECT * from user_posts WHERE sync_state = :syncState")
    suspend fun getPostsBySyncState(syncState: SyncState): List<UserPost>

    @Query("SELECT post_remote_id from user_posts WHERE sync_state != :syncState")
    suspend fun getPendingPosts(syncState: SyncState = SyncState.UP_TO_DATE): List<String>

    @Query("SELECT is_creator from user_posts WHERE post_remote_id = :postRemoteId")
    suspend fun isCreator(postRemoteId: String): Boolean?

    /**
     * Update
     */

    @Update
    suspend fun update(userPost: UserPost)

    /**
     * Delete
     */

    @Delete
    suspend fun deletePosts(posts: List<UserPost>)

    @Query("DELETE FROM user_posts WHERE sync_state = :syncState AND post_remote_id NOT IN (:except)")
    suspend fun clearPosts(except: List<String>, syncState: SyncState = SyncState.UP_TO_DATE)

    @Query("DELETE FROM post_medias WHERE sync_state = :syncState AND post_media_remote_id NOT IN (:except)")
    suspend fun clearPostMedias(except: List<String>, syncState: SyncState = SyncState.UP_TO_DATE)

    /**
     * Flow
     */

    @Query("SELECT * from user_posts WHERE post_remote_id = :postRemoteId")
    fun getUserPostFlow(postRemoteId: String): Flow<UserPost?>

    @Query("SELECT * from post_medias WHERE parent_post_remote_id = :postRemoteId")
    fun getPostImagesFlow(postRemoteId: String): Flow<List<PostMedia>>

    @Query("SELECT * from user_posts LEFT JOIN post_medias ON user_posts.post_remote_id = post_medias.parent_post_remote_id WHERE user_posts.parent_group_remote_id = :groupRemoteId ORDER BY post_time DESC")
    fun getUserPostsWithImagesFlow(groupRemoteId: String): Flow<Map<UserPost, List<PostMedia>>>

}
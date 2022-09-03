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
    suspend fun insert(userPost: UserPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPosts(userPosts: List<UserPost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostMedias(userPostMedias: List<PostMedia>)

    /**
     * Read
     */

    @Query("SELECT * from user_posts WHERE post_remote_id = :postRemoteId")
    suspend fun getUserPost(postRemoteId: String): UserPost?

    @Query("SELECT * FROM user_posts WHERE post_remote_id IN (:remoteIds)")
    suspend fun getUserPostsByIds(remoteIds: List<String>): List<UserPost>

    @Query("SELECT * FROM post_medias WHERE post_media_remote_id IN (:remoteIds)")
    suspend fun getPostMediasByIds(remoteIds: List<String>): List<PostMedia>


    @Query("SELECT * from user_posts WHERE post_sync_state = :syncState")
    suspend fun getPostsBySyncState(syncState: SyncState): List<UserPost>

    @Query("SELECT is_creator from user_posts WHERE post_remote_id = :postRemoteId")
    suspend fun isCreator(postRemoteId: String): Boolean?

    /**
     * Update
     */

    @Update
    suspend fun update(userPost: UserPost)

    @Update
    suspend fun update(userPosts: List<UserPost>)

    /**
     * Delete
     */

    @Delete
    suspend fun deletePosts(posts: List<UserPost>)

    @Query("DELETE FROM user_posts WHERE post_remote_id NOT IN (:except)")
    suspend fun clearPostsNotIn(except: List<String>)

    @Query("DELETE FROM post_medias WHERE post_media_remote_id NOT IN (:except)")
    suspend fun clearPostMediasNotIn(except: List<String>)

    /**
     * Flow
     */

    @Query("SELECT * from user_posts WHERE post_remote_id = :postRemoteId")
    fun getUserPostFlow(postRemoteId: String): Flow<UserPost?>

    @Query("SELECT * from post_medias WHERE parent_post_remote_id = :postRemoteId")
    fun getPostImagesFlow(postRemoteId: String): Flow<List<PostMedia>>

    @Query("SELECT * from user_posts LEFT JOIN post_medias ON post_remote_id = parent_post_remote_id WHERE parent_group_remote_id = :groupRemoteId ORDER BY post_creation_time DESC")
    fun getUserPostsWithImagesFlow(groupRemoteId: String): Flow<Map<UserPost, List<PostMedia>>>

    @Query("SELECT * from user_posts LEFT JOIN post_medias ON post_remote_id = parent_post_remote_id WHERE post_remote_id = :postRemoteId LIMIT 1")
    fun getUserPostWithImagesFlow(postRemoteId: String): Flow<Map<UserPost, List<PostMedia>>>

}
package com.example.mykotlinapp.dao.impl

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestDataSource
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.PostDao
import com.example.mykotlinapp.model.entity.post.PostMedia
import com.example.mykotlinapp.model.entity.post.UserPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostTestDao : PostDao, TestDao {

    private val userPostsTable = TestDataSource<String, UserPost> { it.remoteId }
    private val postMediasTable = TestDataSource<String, PostMedia> { it.remoteId }

    override suspend fun insert(userPost: UserPost) = userPostsTable.insert(userPost)

    override suspend fun insertUserPosts(userPosts: List<UserPost>) = userPostsTable.insert(userPosts)

    override suspend fun insertPostMedias(userPostMedias: List<PostMedia>) = postMediasTable.insert(userPostMedias)

    override suspend fun getUserPost(postRemoteId: String): UserPost? = userPostsTable.get(postRemoteId)

    override suspend fun getUserPostsByIds(remoteIds: List<String>): List<UserPost> =
        userPostsTable.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getPostMediasByIds(remoteIds: List<String>): List<PostMedia> =
        postMediasTable.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getPostsBySyncState(syncState: SyncState): List<UserPost> =
        userPostsTable.getAllWhen { it.syncState == syncState }

    override suspend fun isCreator(postRemoteId: String): Boolean? = userPostsTable.get(postRemoteId)?.isCreator

    override suspend fun update(userPost: UserPost) = userPostsTable.update(userPost)

    override suspend fun update(userPosts: List<UserPost>) = userPostsTable.updateAll(userPosts)

    override suspend fun deletePosts(posts: List<UserPost>) = userPostsTable.deleteAll(posts)

    override suspend fun clearPostsNotIn(except: List<String>) = userPostsTable.deleteWhen { !except.contains(it.remoteId) }

    override suspend fun clearPostMediasNotIn(except: List<String>) = postMediasTable.deleteWhen { !except.contains(it.remoteId) }

    override fun getUserPostFlow(postRemoteId: String): Flow<UserPost?> = flow {
        emit(getUserPost(postRemoteId))
    }

    override fun getPostImagesFlow(postRemoteId: String): Flow<List<PostMedia>> = flow {
        emit(postMediasTable.getAllWhen { it.postRemoteId == postRemoteId })
    }

    override fun getUserPostsWithImagesFlow(groupRemoteId: String): Flow<Map<UserPost, List<PostMedia>>> = flow {
        emit(userPostsTable.leftJoin(postMediasTable) { userPost, postMedia ->
            userPost.remoteId == postMedia.remoteId && userPost.groupRemoteId == groupRemoteId
        })
    }

    override fun getUserPostWithImagesFlow(postRemoteId: String): Flow<Map<UserPost, List<PostMedia>>> = flow {
        emit(userPostsTable.leftJoin(postMediasTable) { userPost, postMedia ->
            userPost.remoteId == postMedia.remoteId && userPost.remoteId == postRemoteId
        })
    }

    override fun clear() {
        userPostsTable.clear()
        postMediasTable.clear()
    }
}
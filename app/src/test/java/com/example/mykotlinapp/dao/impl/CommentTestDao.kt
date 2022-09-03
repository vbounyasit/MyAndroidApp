package com.example.mykotlinapp.dao.impl

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestDataSource
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.CommentDao
import com.example.mykotlinapp.model.entity.post.UserComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CommentTestDao : CommentDao, TestDao {

    private val commentsTable = TestDataSource<String, UserComment> { it.remoteId }

    override suspend fun insertUserComments(userComments: List<UserComment>) = commentsTable.insert(userComments)

    override suspend fun getUserComment(commentRemoteId: String): UserComment? = commentsTable.get(commentRemoteId)

    override suspend fun getUserCommentsByIds(remoteIds: List<String>): List<UserComment> = commentsTable.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getUserCommentsBySyncState(syncState: SyncState): List<UserComment> = commentsTable.getAllWhen { it.syncState == syncState }

    override suspend fun update(comment: UserComment) = commentsTable.update(comment)

    override suspend fun update(comments: List<UserComment>) = commentsTable.updateAll(comments)

    override suspend fun deleteComments(comments: List<UserComment>) = commentsTable.deleteAll(comments)

    override suspend fun clearCommentsNotIn(except: List<String>) = commentsTable.deleteWhen { except.contains(it.remoteId) }

    override fun getUserCommentsFlow(postRemoteId: String): Flow<List<UserComment>> = flow {
        emit(commentsTable.getAllWhen { it.parentPostRemoteId == postRemoteId })
    }

    override fun clear() = commentsTable.clear()
}
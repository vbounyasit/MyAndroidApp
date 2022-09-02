package com.example.mykotlinapp.dao.impl

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestDataSource
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.GroupDao
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.entity.post.UserPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GroupTestDao : GroupDao, TestDao {

    private val groupTable = TestDataSource<String, GroupProperty> { it.remoteId }
    private val userPostsTable = TestDataSource<String, UserPost> { it.remoteId }

    override suspend fun insert(groupProperty: GroupProperty) = groupTable.insert(groupProperty)

    override suspend fun getGroup(remoteId: String): GroupProperty? = groupTable.get(remoteId)

    override suspend fun getGroupsBySyncState(syncState: SyncState): List<GroupProperty> = groupTable.getAllWhen { it.syncState == syncState }

    override suspend fun isGroupAdmin(remoteId: String): Boolean? = getGroup(remoteId)?.isAdmin

    override suspend fun update(groupProperty: GroupProperty) = groupTable.update(groupProperty)

    override suspend fun update(groups: List<GroupProperty>) = groupTable.updateAll(groups)

    override suspend fun readGroup(groupRemoteId: String, lastReadTime: Long) = groupTable.update(groupRemoteId) { it.copy(lastReadTime = lastReadTime) }

    override fun getGroupFlow(remoteId: String): Flow<GroupProperty?> = flow {
        emit(getGroup(remoteId))
    }

    override fun getUnreadPostsCountFlow(groupRemoteId: String): Flow<Int> = flow {
        emit(
            userPostsTable.join(groupTable) { userPost, group ->
                userPost.groupRemoteId == group.remoteId && group.remoteId == groupRemoteId && group.lastReadTime?.let { userPost.creationTime > it } ?: true
            }.size
        )
    }

    override fun clear() = groupTable.clear()
}
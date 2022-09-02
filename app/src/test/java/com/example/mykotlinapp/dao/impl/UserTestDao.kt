package com.example.mykotlinapp.dao.impl

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestDataSource
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.UserDao
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.entity.user.UserContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserTestDao : UserDao, TestDao {

    private val userTable = TestDataSource<String, User> { it.remoteId }
    private val userContactTable = TestDataSource<String, UserContact> { it.remoteId }

    override suspend fun insert(user: User) = userTable.insert(user)

    override suspend fun insert(userContacts: List<UserContact>) = userContactTable.insert(userContacts)

    override suspend fun getUser(remoteId: String): User? = userTable.get(remoteId)

    override suspend fun getUserBySyncState(remoteId: String, syncState: SyncState): User? =
        userTable.getWhen { it.remoteId == remoteId && it.syncState == syncState }

    override suspend fun getContact(remoteId: String): UserContact? =
        userContactTable.get(remoteId)

    override suspend fun getContactsByIds(remoteIds: List<String>): List<UserContact> =
        userContactTable.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getContactsBySyncState(syncState: SyncState): List<UserContact> =
        userContactTable.getAllWhen { it.syncState == syncState }

    override suspend fun update(user: User) = userTable.update(user)

    override suspend fun update(userContact: UserContact) = userContactTable.update(userContact)

    override suspend fun deleteUser(remoteId: String) = userTable.delete(remoteId)

    override suspend fun deleteContacts(contacts: List<UserContact>) = userContactTable.deleteAll(contacts)

    override suspend fun clearContactsNotIn(except: List<String>) = userContactTable.deleteWhen { !except.contains(it.remoteId) }

    override fun getUserFlow(remoteId: String): Flow<User?> =
        flow {
            emit(userTable.get(remoteId))
        }

    override fun getContactsOfRelationFlow(relationType: ContactRelationType): Flow<List<UserContact>> =
        flow {
            emit(userContactTable.getAllWhen { it.relationType == relationType }.sortedBy { it.fullName })
        }

    override fun getContactsNotOfRelationFlow(relationType: ContactRelationType): Flow<List<UserContact>> =
        flow {
            emit(userContactTable.getAllWhen { it.relationType != relationType }.sortedBy { it.fullName })
        }

    override fun clear() {
        userTable.clear()
        userContactTable.clear()
    }
}
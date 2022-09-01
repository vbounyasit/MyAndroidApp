package com.example.mykotlinapp.dao.impl

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestLocalStorage
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.UserDao
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.entity.user.UserContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserTestDao : UserDao, TestDao {

    private val userLocalStorage = TestLocalStorage<User, String> { it.remoteId }
    private val userContactLocalStorage = TestLocalStorage<UserContact, String> { it.remoteId }

    override suspend fun insert(user: User) {
        userLocalStorage.insert(user)
    }

    override suspend fun insert(userContacts: List<UserContact>) {
        userContactLocalStorage.insert(userContacts)
    }

    override suspend fun getUser(remoteId: String): User? = userLocalStorage.get(remoteId)

    override suspend fun getUserBySyncState(remoteId: String, syncState: SyncState): User? =
        userLocalStorage.getWhen { it.remoteId == remoteId && it.syncState == syncState }

    override suspend fun getContact(remoteId: String): UserContact? =
        userContactLocalStorage.get(remoteId)

    override suspend fun getContactsByIds(remoteIds: List<String>): List<UserContact> =
        userContactLocalStorage.getAllWhen { remoteIds.contains(it.remoteId) }

    override suspend fun getContactsBySyncState(syncState: SyncState): List<UserContact> =
        userContactLocalStorage.getAllWhen { it.syncState == syncState }

    override suspend fun update(user: User) {
        userLocalStorage.update(user)
    }

    override suspend fun update(userContact: UserContact) {
        userContactLocalStorage.update(userContact)
    }

    override suspend fun deleteUser(remoteId: String) {
        userLocalStorage.delete(remoteId)
    }

    override suspend fun deleteContacts(contacts: List<UserContact>) {
        userContactLocalStorage.deleteEntities(contacts)
    }

    override suspend fun clearContactsNotIn(except: List<String>) {
        userContactLocalStorage.deleteWhen { !except.contains(it.remoteId) }
    }

    override fun getUserFlow(remoteId: String): Flow<User?> = flow {
        emit(userLocalStorage.get(remoteId))
    }

    override fun getContactsOfRelationFlow(relationType: ContactRelationType): Flow<List<UserContact>> =
        flow {
            emit(userContactLocalStorage.getAllWhen { it.relationType == relationType })
        }

    override fun getContactsNotOfRelationFlow(relationType: ContactRelationType): Flow<List<UserContact>> =
        flow {
            emit(userContactLocalStorage.getAllWhen { it.relationType != relationType })
        }

    override fun clear() {
        userLocalStorage.clear()
        userContactLocalStorage.clear()
    }
}
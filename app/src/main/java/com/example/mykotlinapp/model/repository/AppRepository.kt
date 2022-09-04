package com.example.mykotlinapp.model.repository

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.model.entity.TimeStampData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * base class implemented by all repositories - work in progress
 * TODO use performAction in all subclasses
 * TODO implement helper functions for sending updates to API
 * TODO implement helper functions to get flows of lists and filter PENDING_REMOVAL
 * TODO implement helper functions to update database with a given input
 * TODO implement helper functions to create API request, handle API responses
 */
open class AppRepository(private val sharedPreferenceDao: SharedPreferenceDao) {

    /**
     * Dispatches and execute an asynchronous action to a specific thread
     *
     * @param dispatcher The dispatcher to use
     * @param action The action to execute
     * @return The Result of the action
     */
    suspend fun <T> executeAction(dispatcher: CoroutineDispatcher, action: suspend () -> T): Result<T> =
        withContext(dispatcher) {
            try {
                Result.success(action())
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }

    /**
     * Dispatches and execute an asynchronous HTTP authenticated action to a specific thread
     *
     * @param T The type of the action result
     * @param dispatcher The dispatcher to use
     * @param action The action to execute with the user authentication token as input
     * @return The Result object containing our result value
     */
    suspend fun <T> executeAuthenticatedAction(dispatcher: CoroutineDispatcher, action: suspend (String) -> T): Result<T> =
        withContext(dispatcher) {
            sharedPreferenceDao.getUserAuthToken()?.let {
                try {
                    Result.success(action("Bearer $it"))
                } catch (exception: Exception) {
                    Result.failure(exception)
                }
            } ?: Result.failure(Exception("Failed to authenticate user for performing action"))
        }

    /**
     * Synchronizes new data to the local database
     * This will make sure that :
     *      - the new data does not override the current data that is queued for update/deletion
     *      - the old data is cleared from the database
     * @param LocalData The type of the database entity
     * @param newData The new data received from the API
     * @param dataBulkOperations The DAO database functions related to the database entity
     */
    protected suspend fun <LocalData> updateData(
        newData: List<LocalData>,
        dataBulkOperations: DataBulkOperations<LocalData>
    ) where LocalData : SyncData, LocalData : TimeStampData = syncData(newData, dataBulkOperations, isLatestData())

    /**
     * Synchronizes a single new entity to the local database
     * This will make sure that :
     *      - the new entity does not override the current data that is queued for update/deletion
     * @param LocalData The type of the database entity
     * @param newEntity The new entity received from the API
     * @param dataOperations The DAO database functions related to the database entity
     */
    protected suspend fun <LocalData> updateEntity(
        newEntity: LocalData,
        dataOperations: DataOperations<LocalData>
    ) where LocalData : SyncData, LocalData : TimeStampData = syncEntity(newEntity, dataOperations, isLatestData())

    /**
     * Replaces local data with the new data
     * This will make sure that :
     *      - the new data does not override the current data that is queued for deletion
     *      - the old data is cleared from the database
     * @param LocalData The type of the database entity
     * @param newData The new data received from the API
     * @param dataBulkOperations The DAO database functions related to the database entity
     */
    protected suspend fun <LocalData : SyncData> replaceLocalData(
        newData: List<LocalData>,
        dataBulkOperations: DataBulkOperations<LocalData>
    ) = syncData(newData, dataBulkOperations, isNotRemoved())

    /**
     * Replaces a local entity with the new one
     * This will make sure that :
     *      - the new entity does not override the current data that is queued for deletion
     * @param LocalData The type of the database entity
     * @param newEntity The new entity received from the API
     * @param dataOperations The DAO database functions related to the database entity
     */
    protected suspend fun <LocalData : SyncData> replaceLocalEntity(
        newEntity: LocalData,
        dataOperations: DataOperations<LocalData>
    ) = syncEntity(newEntity, dataOperations, isNotRemoved())

    /**
     * Main sync functions
     */

    /**
     * Synchronizes new data to the local database based
     * The old and new data are compared based on a predicate
     *
     * @param LocalData The type of the database entity
     * @param newData The new data received from the API
     * @param dataBulkOperations The DAO database functions related to the database entity
     * @param shouldReplaceData The predicate to use to compare the old and new data
     *                       (old data, new data) -> should the new data replace the old one
     */
    private suspend fun <LocalData : SyncData> syncData(
        newData: List<LocalData>,
        dataBulkOperations: DataBulkOperations<LocalData>,
        shouldReplaceData: (LocalData, LocalData) -> Boolean
    ) {
        val (getDataByIds, upsertData, clearDataNotIn) = dataBulkOperations
        val getKey: (LocalData) -> String = { it.remoteId }
        with(newData) {
            val remoteIds = map(getKey)
            val dataByRemoteId = getDataByIds(remoteIds).associateBy(getKey)
            clearDataNotIn?.invoke(remoteIds)
            upsertData(filter { newData ->
                dataByRemoteId[newData.remoteId]?.let { currentData -> shouldReplaceData(currentData, newData) } ?: true
            })
        }
    }

    /**
     * Replaces a local entity with the new one
     * This will make sure that :
     *      - the new entity does not override the current data that is queued for deletion
     * @param LocalData The type of the database entity
     * @param newEntity The new entity received from the API
     * @param dataOperations The DAO database functions related to the database entity
     * @param shouldKeepData The predicate to use to compare the old and new entity
     *                       (old entity, new entity) -> should the new entity replace the old one
     */
    private suspend fun <LocalData : SyncData> syncEntity(
        newEntity: LocalData,
        dataOperations: DataOperations<LocalData>,
        shouldKeepData: (LocalData, LocalData) -> Boolean,
    ) {
        val (getDataById, upsertData) = dataOperations
        syncData(
            listOf(newEntity),
            DataBulkOperations(
                getDataByIds = { listOfNotNull(it.firstOrNull()?.let { remoteId -> getDataById(remoteId) }) },
                upsertData = { data -> data.firstOrNull()?.let { upsertData(it) } }
            ),
            shouldKeepData
        )
    }

    /**
     * Synchronization predicates
     */

    /**
     * Represents the predicate for comparing old and new data based on their update time
     *
     * @param LocalData The type of the database entity
     * @return whether the new data is newer than the current data
     */
    private fun <LocalData> isLatestData(): (LocalData, LocalData) -> Boolean
            where LocalData : SyncData, LocalData : TimeStampData = { current, new ->
        current.syncState != SyncState.PENDING_REMOVAL && current.updateTime < new.updateTime
    }

    /**
     * Represents the predicate for only removing data that are not currently pending deletion
     *
     * @param LocalData The type of the database entity
     * @return whether the old data is not pending deletion
     */
    private fun <LocalData : SyncData> isNotRemoved(): (LocalData, LocalData) -> Boolean =
        { current, _ -> current.syncState != SyncState.PENDING_REMOVAL }

    /**
     * Data class containing different database DAO functions for data synchronization
     *
     * @param LocalData The type of the database entity
     * @property getDataByIds The DAO function for retrieving data based on their remote id
     * @property upsertData The DAO function for performing bulk data upsert
     * @property clearDataNotIn The DAO function for cleaning irrelevant data
     */
    protected data class DataBulkOperations<LocalData : SyncData>(
        val getDataByIds: suspend (List<String>) -> List<LocalData>,
        val upsertData: suspend (List<LocalData>) -> Unit,
        val clearDataNotIn: (suspend (List<String>) -> Unit)? = null
    )

    /**
     * Data class containing different database DAO functions for data synchronization
     *
     * @param LocalData The type of the database entity
     * @property getDataById The DAO function for retrieving the entity based on its remote id
     * @property upsertData The DAO function for performing data upsert
     */
    protected data class DataOperations<LocalData : SyncData>(
        val getDataById: suspend (String) -> LocalData?,
        val upsertData: suspend (LocalData) -> Unit
    )

}
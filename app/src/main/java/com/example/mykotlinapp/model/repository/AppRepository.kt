package com.example.mykotlinapp.model.repository

import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * base class implemented by all repositories - work in progress
 * TODO use performAction in all subclasses
 * TODO implements specific functions for syncing local database to remote API (clean data/ update data/ etc)
 */
abstract class AppRepository(private val sharedPreferenceDao: SharedPreferenceDao) {

    suspend fun performAction(dispatcher: CoroutineDispatcher, action: suspend () -> Unit): Result<Unit> =
        withContext(dispatcher) {
            try {
                Result.success(action())
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }


    suspend fun <T> performAuthenticatedAction(dispatcher: CoroutineDispatcher, action: suspend (String) -> T): Result<T> =
        withContext(dispatcher) {
            sharedPreferenceDao.getUserAuthToken()?.let {
                try {
                    Result.success(action("Bearer $it"))
                } catch (exception: Exception) {
                    Result.failure(exception)
                }
            } ?: Result.failure(Exception("Failed to authenticate user for performing action"))
        }
}
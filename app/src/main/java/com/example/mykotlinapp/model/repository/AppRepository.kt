package com.example.mykotlinapp.model.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * base class implemented by all repositories - work in progress
 * TODO use performAction in all subclasses
 * TODO implements specific functions for syncing local database to remote API (clean data/ update data/ etc)
 */
abstract class AppRepository {

    suspend fun performAction(dispatcher: CoroutineDispatcher, action: suspend () -> Unit): Result<Unit> {
        return withContext(dispatcher) {
            try {
                Result.success(action())
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }
    }
}
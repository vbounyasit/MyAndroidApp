package com.example.mykotlinapp.model.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

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
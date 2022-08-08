package com.example.mykotlinapp.background_work.workmanager.contact

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mykotlinapp.model.repository.impl.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RemoveContactsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            userRepository.sendDeleteContactRequests()
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }
}
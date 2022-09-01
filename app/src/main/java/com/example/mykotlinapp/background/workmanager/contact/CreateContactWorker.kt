package com.example.mykotlinapp.background.workmanager.contact

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.mykotlinapp.WORK_CREATE_CONTACT_INPUT_KEY
import com.example.mykotlinapp.model.repository.impl.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreateContactWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            inputData.getString(WORK_CREATE_CONTACT_INPUT_KEY)?.let {
                val outputData = workDataOf(WORK_CREATE_CONTACT_INPUT_KEY to it)
                if (userRepository.createContactRequest(it).isSuccess)
                    Result.success(outputData)
                else
                    Result.failure()
            } ?: Result.failure()
        } catch (exception: Exception) {
            Result.failure()
        }
    }
}
package com.example.mykotlinapp.background_work.workmanager.chat

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mykotlinapp.model.repository.impl.GroupRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateGroupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val groupRepository: GroupRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            groupRepository.sendUpdateGroups()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
package com.example.mykotlinapp.background_work.workmanager.post

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mykotlinapp.model.repository.impl.CommentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateCommentsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val commentRepository: CommentRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            commentRepository.sendUpdateComments()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}
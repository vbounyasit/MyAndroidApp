package com.example.mykotlinapp.background_work.workmanager.post

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mykotlinapp.model.repository.impl.PostRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RemovePostsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val postRepository: PostRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            postRepository.sendDeletePosts()
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }
}
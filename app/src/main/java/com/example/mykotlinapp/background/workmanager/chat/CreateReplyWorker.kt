package com.example.mykotlinapp.background.workmanager.chat

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mykotlinapp.WORK_REPLY_CHAT_CONTENT_INPUT_KEY
import com.example.mykotlinapp.WORK_REPLY_CHAT_REMOTE_ID_INPUT_KEY
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatLogInput
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreateReplyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val chatRepository: ChatRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val chatRemoteId: String? = inputData.getString(WORK_REPLY_CHAT_REMOTE_ID_INPUT_KEY)
            val replyContent: String? = inputData.getString(WORK_REPLY_CHAT_CONTENT_INPUT_KEY)
            if (chatRemoteId != null && replyContent != null) {
                chatRepository.createChatLog(CreateChatLogInput(chatRemoteId, replyContent))
                chatRepository.sendCreateChatLogs()
                Result.success()
            } else Result.failure()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
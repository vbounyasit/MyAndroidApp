package com.example.mykotlinapp.background_work.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import androidx.work.Data
import androidx.work.WorkManager
import com.example.mykotlinapp.R
import com.example.mykotlinapp.WORK_REPLY_CHAT_CONTENT_INPUT_KEY
import com.example.mykotlinapp.WORK_REPLY_CHAT_REMOTE_ID_INPUT_KEY
import com.example.mykotlinapp.background_work.workmanager.chat.CreateReplyWorker
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.RegularBackgroundTask
import com.example.mykotlinapp.ui.AppViewModel.Companion.launchNetworkBackgroundTask
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReplyMessageReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteInput = intent?.let { RemoteInput.getResultsFromIntent(it) }
        remoteInput?.let {
            val chatRemoteId: String? = intent.getStringExtra(context?.getString(R.string.chat_remote_id))
            val replyContent: String? = it.getCharSequence(context?.getString(R.string.chat_notification_reply_key))?.toString()
            if (chatRemoteId != null && replyContent != null) {
                val inputData: Data = run {
                    val builder = Data.Builder()
                    builder.putString(WORK_REPLY_CHAT_REMOTE_ID_INPUT_KEY, chatRemoteId)
                    builder.putString(WORK_REPLY_CHAT_CONTENT_INPUT_KEY, replyContent)
                    builder.build()
                }
                workManager.launchNetworkBackgroundTask<CreateReplyWorker>(RegularBackgroundTask, inputData)
            }
        }
    }
}
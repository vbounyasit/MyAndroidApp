package com.example.mykotlinapp.ui.components.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.bumptech.glide.Glide
import com.example.mykotlinapp.R
import com.example.mykotlinapp.activities.MainActivity
import com.example.mykotlinapp.background_work.receiver.ReplyMessageReceiver
import com.example.mykotlinapp.model.dto.ui.chat.ChatItemDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatLogDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Helper class for in app notifications
 * @Injected in an activity
 *
 * @property notificationManager The notification manager service instance
 * @property context The application context
 * @property dispatcher The dispatcher to dispatch the coroutine
 */
class NotificationComponent(
    private val notificationManager: NotificationManager,
    val context: Context,
    val dispatcher: CoroutineDispatcher,
) {

    private var currentActiveChatRemoteId: String? = null

    fun setCurrentChat(remoteId: String) {
        currentActiveChatRemoteId = remoteId
    }

    fun resetCurrentChat() {
        currentActiveChatRemoteId = null
    }

    /**
     * Sends a chat notification to the user when he receives a new message
     *
     * @param notification The notification data object to send
     */
    suspend fun sendChatNotification(notification: ChatLogsAppNotification) {
        if (notification.chatLogs.isEmpty() || currentActiveChatRemoteId == notification.chatItem.remoteId) return
        return withContext(dispatcher) {
            try {
                val contentIntent = Intent(context, MainActivity::class.java)
                    .putExtra(context.getString(R.string.chat_remote_id), notification.chatItem.remoteId)
                val contentPendingIntent = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val replyInput: RemoteInput = RemoteInput.Builder(context.getString(R.string.chat_notification_reply_key))
                    .setLabel(context.getString(R.string.notification_reply_label))
                    .build()
                val replyIntent = Intent(context, ReplyMessageReceiver::class.java)
                    .putExtra(context.getString(R.string.chat_remote_id), notification.chatItem.remoteId)
                val replyPendingIntent =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        PendingIntent.getBroadcast(
                            context,
                            NOTIFICATION_ID,
                            replyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    else null
                val replyAction = NotificationCompat.Action.Builder(
                    R.drawable.ic_post_reply,
                    context.getString(R.string.notification_reply_icon_title),
                    replyPendingIntent
                ).addRemoteInput(replyInput).build()

                val pictureUrl: String? = if (notification.chatItem.profilePictures.size == 1) notification.chatItem.profilePictures.first() else null
                val bitMap = pictureUrl?.let { Glide.with(context).asBitmap().load(it).submit().get() }
                val me = Person.Builder()
                    .setName(context.getString(R.string.chat_item_author_me))
                    .setKey(notification.userRemoteId)
                    .build()

                var notificationStyle = NotificationCompat.MessagingStyle(me)
                    .setGroupConversation(notification.isGroupConversation)
                    .setConversationTitle(notification.chatItem.name)

                notification.chatLogs.forEach { chatLog ->
                    val sender: Person? = chatLog.author?.let {
                        Person.Builder().setName(it.firstName)
                            .setKey(it.remoteId)
                            .build()
                    }
                    notificationStyle = notificationStyle
                        .addMessage(
                            chatLog.content,
                            chatLog.creationTimeStamp,
                            sender)
                }

                val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.chat_notification_channel_key))
                    .setStyle(notificationStyle)
                    .setContentIntent(contentPendingIntent)
                    .addAction(replyAction)
                    .setSmallIcon(R.drawable.ic_chat_bubble)
                    .setLargeIcon(bitMap)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                notificationManager.notify(notification.chatItem.remoteId, NOTIFICATION_ID, notificationBuilder.build())

            } catch (exception: Exception) {
                exception.message?.let { Log.e(TAG, it) }
            }
        }
    }

    /**
     * Creates a chat notification channel to send chat notifications on
     * Note : After API 26, channels are required for notifications
     *
     * @param channelId The channel ID to create
     * @param channelName The name of the channel
     */
    fun createChatChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = context.getString(R.string.chat_notification_channel_desc)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    sealed interface AppNotification

    /**
     * Chat logs app notification
     *
     * @property userRemoteId The remote id of the user related to the notification
     * @property chatItem The chat item the notification is about
     * @property isGroupConversation Whether or not the chat is a group conversation (for notification styling)
     * @property chatLogs The list of chat logs to include in the notification
     */
    data class ChatLogsAppNotification(
        val userRemoteId: String,
        val chatItem: ChatItemDTO,
        val isGroupConversation: Boolean,
        val chatLogs: List<ChatLogDTO>,
    ) : AppNotification

    companion object {
        const val TAG = "NotificationComponent"
        const val NOTIFICATION_ID = 0
    }
}
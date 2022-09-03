package com.example.mykotlinapp.repositories.impl

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.dao.impl.ChatTestDao
import com.example.mykotlinapp.dao.impl.GroupTestDao
import com.example.mykotlinapp.domain.pojo.ActivityStatus
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.ui.chat.ChatItemDTO
import com.example.mykotlinapp.model.dto.ui.chat.ChatListItemDTO
import com.example.mykotlinapp.model.entity.chat.ChatItem
import com.example.mykotlinapp.model.entity.chat.ChatLog
import com.example.mykotlinapp.model.entity.user.UserContact
import com.example.mykotlinapp.model.mappers.impl.Utils
import com.example.mykotlinapp.model.mappers.impl.chat.ChatItemMapper
import com.example.mykotlinapp.model.mappers.impl.chat.ChatLogMapper
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.network.service.ChatApiService
import com.example.mykotlinapp.ui.components.notifications.NotificationComponent.ChatLogsAppNotification
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryTest : WordSpec({

    val authUserRemoteId = "authUserRemoteId"

    lateinit var chatRepository: ChatRepository
    val chatDao = ChatTestDao()
    val groupDao = GroupTestDao()

    val context = mockk<Context>(relaxed = true)
    every { context.getString(R.string.profile_pictures_delimiter) } returns ", "

    val sharedPreferenceDao = mockk<SharedPreferenceDao>()
    coEvery { sharedPreferenceDao.getAuthUserRemoteId() } returns authUserRemoteId

    val apiService = mockk<ChatApiService>()

    mockkObject(Utils)
    every { Utils.toFormattedTimeAgo(any(), any(), any()) } returns "Now"
    every { Utils.toChatLogTime(any(), any()) } returns "Now"

    beforeTest {
        runTest {
            chatRepository = ChatRepository(
                context,
                UnconfinedTestDispatcher(),
                sharedPreferenceDao,
                apiService,
                chatDao,
                groupDao
            )
        }
    }

    afterEach { chatDao.clear() }

    fun newContact(remoteId: String, firstName: String = "firstName", lastName: String = "lastName", relationType: ContactRelationType = ContactRelationType.FRIENDS) = UserContact(
        remoteId, firstName, lastName, "$firstName $lastName", "profile", "desc",
        0, relationType, SyncState.UP_TO_DATE
    )

    fun newChatLog(remoteId: String, chatRemoteId: String, authorFirstName: String, content: String, isMe: Boolean, creationTime: Long = 0) =
        ChatLog(remoteId, chatRemoteId, newContact("remoteId", authorFirstName), content, isMe, SyncState.UP_TO_DATE, creationTime, 0)

    fun newChatItem(remoteId: String, lastActive: Long, lastReadTime: Long?) =
        ChatItem(remoteId, "name", "profile", lastActive, lastReadTime, false, SyncState.UP_TO_DATE, 0, 0)

    "getChatItems" should {
        "returns the chat item with the latest chat log" {
            //Given
            val chatItem = newChatItem("remote", 0, null)
            val lastChatLog = newChatLog("remote4", "remote", "first4", "test4", true, 5)
            chatDao.insertChatItems(listOf(chatItem))
            chatDao.insertChatLogs(
                listOf(
                    newChatLog("remote1", "remote", "first1", "test1", true, 1),
                    newChatLog("remote2", "remote", "first2", "test2", true, 2),
                    newChatLog("remote3", "remote", "first3", "test3", true, 4),
                    lastChatLog,
                )
            )
            //When
            val chatItems: List<ChatListItemDTO> = chatRepository.getChatItems().first()
            //Then
            chatItems shouldBe listOf(
                ChatListItemDTO(
                    ChatItemDTO(chatItem.remoteId, chatItem.name, listOf(chatItem.profilePicture), ActivityStatus.AWAY, null, false),
                    ChatLogMapper.toDTO(context)(lastChatLog),
                    false
                )
            )
        }
    }

    "getChatLogsAppNotifications" should {
        "compute the right result" {
            runTest {
                //Given
                val chatItem1 = newChatItem("remote1", 0, 5)
                val chatItem2 = newChatItem("remote2", 0, 5)
                val chatLog1 = newChatLog("remote1", "remote1", "first1", "test1", true, 1)
                val chatLog2 = newChatLog("remote2", "remote1", "first2", "test2", true, 2)
                val chatLog3 = newChatLog("remote3", "remote2", "first3", "test3", true, 4)
                val chatLog4 = newChatLog("remote4", "remote1", "first4", "test4", true, 8)
                val chatLog5 = newChatLog("remote5", "remote1", "first6", "test5", true, 9)
                chatDao.insertChatItems(listOf(chatItem1, chatItem2))
                chatDao.insertChatLogs(listOf(chatLog1, chatLog2, chatLog3, chatLog4, chatLog5))
                //When
                val chatNotifications = chatRepository.getChatLogsAppNotifications(listOf("remote1", "remote2", "remote3", "remote4", "remote5"))
                //Then
                chatNotifications.getOrNull() shouldBe listOf(
                    ChatLogsAppNotification(
                        authUserRemoteId,
                        ChatItemMapper.toDTO(context)(chatItem1),
                        false,
                        listOf(chatLog4, chatLog5).map(ChatLogMapper.toDTO(context))
                    )
                )
            }
        }
    }


})
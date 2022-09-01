package com.example.mykotlinapp.mappers.impl

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.chat.ChatListItemDTO
import com.example.mykotlinapp.model.entity.chat.ChatItem
import com.example.mykotlinapp.model.entity.chat.ChatLog
import com.example.mykotlinapp.model.entity.user.UserContact
import com.example.mykotlinapp.model.mappers.impl.Utils
import com.example.mykotlinapp.model.mappers.impl.chat.ChatListItemMapper
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class ChatListItemMapperTest : WordSpec({
    val context = mockk<Context>(relaxed = true)
    val delimiter = " : "
    val me = "You"
    every { context.getString(R.string.chat_log_author_delimiter) } returns delimiter
    every { context.getString(R.string.chat_item_author_me) } returns me
    mockkObject(Utils)
    every { Utils.toTimeAgo(any(), any(), any()) } returns "Now"
    every { Utils.toChatLogTime(any(), any()) } returns "Now"

    fun getChatItem(remoteId: String, isGroupChat: Boolean): ChatItem =
        ChatItem(remoteId, "chatName", "picture1", 0, null, isGroupChat, SyncState.UP_TO_DATE, 0, 0)

    fun getChatLog(
        chatLogRemoteId: String,
        authorRemoteId: String,
        authorFirstName: String,
        authorLastName: String,
        isMe: Boolean
    ): ChatLog =
        ChatLog(
            chatLogRemoteId, "chat_$chatLogRemoteId",
            UserContact(
                authorRemoteId,
                authorFirstName,
                authorLastName,
                "fullName",
                "picture",
                null,
                0,
                ContactRelationType.NONE,
                SyncState.UP_TO_DATE
            ),
            "content",
            isMe,
            SyncState.UP_TO_DATE,
            0,
            0
        )

    data class Result(val chatItemRemoteId: String, val read: Boolean, val logContent: String)

    fun ChatListItemDTO.toResult(): Result = Result(chatItem.remoteId, read, lastChatLog.content)

    "ChatListItemMapperTest.toDTO" When {
        "Last chat log author IS ME" should {
            val baseInput = Pair(
                getChatItem("remoteId1", true),
                getChatLog("chatLogRemoteId1", "authorMe", "myName", "myLastName", true)
            )
            "with chat read time < last log creation date - compute the correct content and be READ" {
                //Given
                val input: Pair<ChatItem, ChatLog> = with(baseInput) {
                    copy(first.copy(lastReadTime = 5), second.copy(creationTime = 10))
                }
                //When
                val chatListItemDTO: ChatListItemDTO = ChatListItemMapper.toDTO(context)(input)
                //Then
                chatListItemDTO.toResult() shouldBe Result(
                    "remoteId1",
                    true,
                    "$me${delimiter}content"
                )
            }
            "with chat read time >= last log creation date - compute the correct content and be READ" {
                //Given
                val input: Pair<ChatItem, ChatLog> = with(baseInput) {
                    copy(first.copy(lastReadTime = 10), second.copy(creationTime = 5))
                }
                //When
                val chatListItemDTO: ChatListItemDTO = ChatListItemMapper.toDTO(context)(input)
                //Then
                chatListItemDTO.toResult() shouldBe Result(
                    "remoteId1",
                    true,
                    "$me${delimiter}content"
                )
            }
        }
        "Last chat log author IS NOT ME" should {
            val baseInput = Pair(
                getChatItem("remoteId1", true),
                getChatLog(
                    "chatLogRemoteId1",
                    "authorRemoteId",
                    "authorFirstName",
                    "authorLastName",
                    false
                )
            )
            "with chat read time < last log creation date - compute the correct content and be NOT READ" {
                //Given
                val input: Pair<ChatItem, ChatLog> = with(baseInput) {
                    copy(first.copy(lastReadTime = 5), second.copy(creationTime = 10))
                }
                //When
                val chatListItemDTO: ChatListItemDTO = ChatListItemMapper.toDTO(context)(input)
                //Then
                chatListItemDTO.toResult() shouldBe Result(
                    "remoteId1",
                    false,
                    "${baseInput.second.author.firstName}${delimiter}content"
                )
            }
            "with chat read time >= last log creation date - compute the correct content and be read" {
                //Given
                val input: Pair<ChatItem, ChatLog> = with(baseInput) {
                    copy(first.copy(lastReadTime = 10), second.copy(creationTime = 5))
                }
                //When
                val chatListItemDTO: ChatListItemDTO = ChatListItemMapper.toDTO(context)(input)
                //Then
                chatListItemDTO.toResult() shouldBe Result(
                    "remoteId1",
                    true,
                    "${baseInput.second.author.firstName}${delimiter}content"
                )
            }
        }
    }
})
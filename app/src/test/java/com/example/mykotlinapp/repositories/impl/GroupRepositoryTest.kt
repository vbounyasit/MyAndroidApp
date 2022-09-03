package com.example.mykotlinapp.repositories.impl

import android.content.Context
import com.example.mykotlinapp.dao.impl.ChatTestDao
import com.example.mykotlinapp.dao.impl.GroupTestDao
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateGroupInput
import com.example.mykotlinapp.model.entity.chat.ChatProperty
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.repository.impl.GroupRepository
import com.example.mykotlinapp.network.service.GroupApiService
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GroupRepositoryTest : WordSpec({

    lateinit var groupRepository: GroupRepository
    val chatDao = ChatTestDao()
    val groupDao = GroupTestDao()
    val context = mockk<Context>(relaxed = true)
    val sharedPreferenceDao = mockk<SharedPreferenceDao>()
    val apiService = mockk<GroupApiService>()

    beforeTest {
        runTest {
            groupRepository = GroupRepository(
                context,
                UnconfinedTestDispatcher(),
                sharedPreferenceDao,
                apiService,
                groupDao,
                chatDao
            )
        }
    }

    afterEach { chatDao.clear() }

    fun newChat(remoteId: String, groupRemoteId: String) =
        ChatProperty(remoteId, groupRemoteId, "name", "profile", 0, 0, true, SyncState.UP_TO_DATE, 0, 0)

    fun newGroup(remoteId: String, chatRemoteId: String) =
        GroupProperty(remoteId, chatRemoteId, "name", "profile", null, null, 0, null, true, SyncState.UP_TO_DATE, 0, 0)

    "updateGroup" should {
        "correctly update group and chat together" {
            runTest {
                //Given
                val (chatRemoteId, groupRemoteId) = Pair("chatRemoteId", "groupRemoteId")
                val (group, chat) = Pair(newGroup(groupRemoteId, chatRemoteId), newChat(chatRemoteId, groupRemoteId))
                groupDao.insert(group)
                chatDao.insert(chat)
                //When
                val updateGroupInput = UpdateGroupInput(groupRemoteId, "newName", "newDescription")
                groupRepository.updateGroup(updateGroupInput)
                //Then
                groupDao.getGroup(groupRemoteId) shouldBe group.copy(groupName = "newName", groupDescription = "newDescription", syncState = SyncState.PENDING_UPDATE)
                chatDao.getChat(chatRemoteId) shouldBe chat.copy(name = "newName", syncState = SyncState.PENDING_UPDATE)
            }
        }
    }

    "sendUpdateGroupChats" should {
        //todo
    }
})
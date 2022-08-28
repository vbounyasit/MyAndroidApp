package com.example.mykotlinapp

import android.content.Context
import com.example.mykotlinapp.dao.UserTestDao
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.Gender
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.AppDatabase
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.inputs.form.user.UpdateUserInput
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.entity.user.UserContact
import com.example.mykotlinapp.model.mappers.impl.user.UserSettingMapper
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.network.ApiService
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest : WordSpec({

    val authUserRemoteId = "authUserRemoteId"
    val userContactRemoteId = "contactRemoteId"

    val context = mockk<Context>()
    val apiService = mockk<ApiService>()
    val appDatabase = mockk<AppDatabase>()
    val userSettingsDao = mockk<UserSettingMapper>()

    val sharedPreferenceDao = mockk<SharedPreferenceDao>()
    coEvery { sharedPreferenceDao.getAuthUserRemoteId() } returns authUserRemoteId

    lateinit var userRepository: UserRepository
    val userDao = UserTestDao()

    val user = User(
        authUserRemoteId,
        "mail1",
        "firstName1",
        "lastName1",
        "profile",
        "bg",
        "desc",
        Gender.MALE,
        15,
        SyncState.UP_TO_DATE
    )

    val userContact = UserContact(
        userContactRemoteId,
        "firstName1",
        "lastName1",
        "fullName1",
        "profile",
        "desc",
        0,
        ContactRelationType.FRIENDS,
        SyncState.UP_TO_DATE
    )

    beforeTest {
        runTest {
            userRepository = UserRepository(
                context,
                apiService,
                appDatabase,
                UnconfinedTestDispatcher(),
                userDao,
                sharedPreferenceDao,
                userSettingsDao
            )
        }
    }
    afterEach {
        userDao.clear()
    }

    "updateUser" should {
        "correctly update a user" {
            runTest {
                //Given
                userDao.insert(user)
                val updateUserInput =
                    UpdateUserInput(
                        "firstNameUpdated",
                        "lastNameUpdated",
                        "mailUpdated",
                        "descUpdated"
                    )
                //When
                userRepository.updateUser(updateUserInput)
                //Then
                val updatedUser = userDao.getUser(authUserRemoteId)
                updatedUser shouldBe user.copy(
                    firstName = "firstNameUpdated",
                    lastName = "lastNameUpdated",
                    email = "mailUpdated",
                    description = "descUpdated",
                    syncState = SyncState.PENDING_UPDATE
                )
            }
        }
    }

    "submitContactForDeletion" should {
        "correctly update a contact for deletion" {
            runTest {
                //Given
                userDao.insert(user)
                userDao.insert(listOf(userContact))
                //When
                userRepository.submitContactForDeletion(userContactRemoteId)
                //Then
                userDao.getContact(userContactRemoteId) shouldBe userContact.copy(syncState = SyncState.PENDING_REMOVAL)
            }
        }
    }

})
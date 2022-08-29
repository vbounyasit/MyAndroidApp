package com.example.mykotlinapp

import android.content.Context
import com.example.mykotlinapp.dao.UserTestDao
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.Gender
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.AppDatabase
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dto.inputs.form.user.UpdateUserInput
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.entity.user.UserContact
import com.example.mykotlinapp.model.mappers.impl.user.UserContactMapper
import com.example.mykotlinapp.model.mappers.impl.user.UserMapper
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.network.service.UserApiService
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest : WordSpec({

    val authUserRemoteId = "authUserRemoteId"
    val userContactRemoteId = "contactRemoteId"

    val apiService = mockk<UserApiService>()
    val appDatabase = mockk<AppDatabase>()
    val sharedPreferenceDao = mockk<SharedPreferenceDao>()
    coEvery { sharedPreferenceDao.getAuthUserRemoteId() } returns authUserRemoteId

    val context = mockk<Context>(relaxed = true)
    every { context.resources.getInteger(R.integer.minutes_to_inactive) } returns 5
    every { context.resources.getInteger(R.integer.max_contact_requests_to_display) } returns 2

    lateinit var userRepository: UserRepository
    val userDao = UserTestDao()

    val user = User(
        authUserRemoteId, "mail1", "firstName1", "lastName1", "profile", "bg", "desc",
        Gender.MALE, 15, SyncState.UP_TO_DATE
    )

    fun newContact(firstName: String = "firstName", lastName: String = "lastName", relationType: ContactRelationType = ContactRelationType.FRIENDS) = UserContact(
        userContactRemoteId, firstName, lastName, "$firstName $lastName", "profile", "desc",
        0, relationType, SyncState.UP_TO_DATE
    )

    beforeTest {
        runTest {
            userRepository = UserRepository(
                context,
                apiService,
                appDatabase,
                UnconfinedTestDispatcher(),
                userDao,
                sharedPreferenceDao
            )
        }
    }
    afterEach { userDao.clear() }

    "getUserData" should {
        "compute the correct data" {
            runTest {
                //Given
                userDao.insert(user)
                //When
                val userData = userRepository.getUserData(authUserRemoteId).first()
                val expected = UserMapper.toDTO(user)
                //Then
                userData shouldBe expected
            }
        }
    }

    "getUserContactsWithRequests and getUserContacts" should {
        val contacts = listOf(
            newContact("first4", "last4", ContactRelationType.FRIENDS),
            newContact("first1", "last1", ContactRelationType.INCOMING),
            newContact("first2", "last2", ContactRelationType.INCOMING),
            newContact("first3", "last3", ContactRelationType.OUTGOING),
            newContact("first5", "last5", ContactRelationType.FRIENDS),
            newContact("first6", "last6", ContactRelationType.FRIENDS),
        )
        "get friend contacts when we call getUserContacts" {
            runTest {
                //Given
                userDao.insert(contacts)
                //When
                val userContacts = userRepository.getUserContacts().first()
                val expected: List<UserContactDTO> = listOf(
                    newContact("first4", "last4", ContactRelationType.FRIENDS),
                    newContact("first5", "last5", ContactRelationType.FRIENDS),
                    newContact("first6", "last6", ContactRelationType.FRIENDS),
                ).map((UserContactMapper::toDTO)(context))
                //Then
                userContacts shouldBe expected
            }
        }
        "get contacts and request when we call getUserContactsWithRequests" {
            runTest {
                //Given
                userDao.insert(contacts)
                //When
                val userContacts = userRepository.getUserContactsWithRequests().first()
                val expected: List<UserContactDTO> = listOf(
                    newContact("first1", "last1", ContactRelationType.INCOMING),
                    newContact("first2", "last2", ContactRelationType.INCOMING),
                    newContact("first4", "last4", ContactRelationType.FRIENDS),
                    newContact("first5", "last5", ContactRelationType.FRIENDS),
                    newContact("first6", "last6", ContactRelationType.FRIENDS),
                ).map((UserContactMapper::toDTO)(context))
                //Then
                userContacts shouldBe expected
            }
        }
    }

    "updateUser" should {
        "correctly update a user" {
            runTest {
                //Given
                userDao.insert(user)
                val updateUserInput =
                    UpdateUserInput("firstNameUpdated", "lastNameUpdated", "mailUpdated", "descUpdated")
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
                val contact = newContact()
                userDao.insert(user)
                userDao.insert(listOf(contact))
                //When
                userRepository.submitContactForDeletion(userContactRemoteId)
                //Then
                userDao.getContact(userContactRemoteId) shouldBe contact.copy(syncState = SyncState.PENDING_REMOVAL)
            }
        }
    }
})
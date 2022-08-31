package com.example.mykotlinapp.model.repository.impl

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.di.Qualifiers.IoDispatcher
import com.example.mykotlinapp.domain.pojo.ContactRelationType.*
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.AppDatabase
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.dao.UserDao
import com.example.mykotlinapp.model.dto.inputs.form.user.CreateUserInput
import com.example.mykotlinapp.model.dto.inputs.form.user.LogInUserInput
import com.example.mykotlinapp.model.dto.inputs.form.user.UpdateUserInput
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.dto.ui.user.UserDTO
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.entity.user.UserContact
import com.example.mykotlinapp.model.mappers.impl.user.UserContactMapper
import com.example.mykotlinapp.model.mappers.impl.user.UserMapper
import com.example.mykotlinapp.model.mappers.impl.user.UserSettingMapper
import com.example.mykotlinapp.model.mappers.impl.user.create.CreateUserMapper
import com.example.mykotlinapp.model.mappers.impl.user.update.UpdateUserUpdateMapper
import com.example.mykotlinapp.model.repository.AppRepository
import com.example.mykotlinapp.network.dto.requests.user.LogInUserRequest
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserRequest
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserSettingsRequest
import com.example.mykotlinapp.network.dto.requests.user.contact.CreateContactRequest
import com.example.mykotlinapp.network.dto.requests.user.contact.RemoveContactIdRequest
import com.example.mykotlinapp.network.dto.responses.user.UserLoginResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserResponse
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserSettingsResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.UserContactListResponse
import com.example.mykotlinapp.network.service.UserApiService
import com.example.mykotlinapp.ui.components.SharedPreferenceLiveData.Companion.SharedPreferenceBooleanLiveData
import com.example.mykotlinapp.ui.components.SharedPreferenceLiveData.Companion.SharedPreferenceStringLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val userApiService: UserApiService,
    private val appDatabase: AppDatabase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val userDao: UserDao,
    private val sharedPreferenceDao: SharedPreferenceDao,
) : AppRepository(sharedPreferenceDao) {

    /**
     * Shared prefs
     */

    /**
     * Creates a livedata for the local user remote id saved in shared preferences
     * @return The generated livedata instance
     */
    fun newUserRemoteIdLiveData(): SharedPreferenceStringLiveData =
        sharedPreferenceDao.newUserRemoteIdLiveData()

    /**
     * Creates a livedata for the dark mode flag saved in shared preferences
     * @return The generated livedata instance
     */
    fun newDarkModeLiveData(): SharedPreferenceBooleanLiveData =
        sharedPreferenceDao.newDarkModeLiveData()

    /**
     * @return Whether dark mode is enabled or not
     */
    suspend fun isDarkMode(): Boolean = sharedPreferenceDao.isDarkMode()

    /**
     * Update dark mode
     *
     * @param darkMode Whether to enable dark mode or not
     */
    suspend fun updateDarkMode(darkMode: Boolean) = sharedPreferenceDao.updateDarkMode(darkMode)

    /**
     * @return The locally saved user auth token in shared preferences
     */
    suspend fun getUserAuthToken(): String? = sharedPreferenceDao.getUserAuthToken()

    /**
     * Retrieve from local database
     */

    /**
     * Gets a user data
     *
     * @param userRemoteId The remote id of the user ti retrieve
     * @return A flow containing the user data
     */
    fun getUserData(userRemoteId: String): Flow<UserDTO?> {
        return userDao.getUserFlow(userRemoteId).distinctUntilChanged().map { user ->
            user?.let { UserMapper.toDTO(user) }
        }
    }

    /**
     * Gets the list of user contacts with contact requests
     *
     * @return A flow containing the contacts with requests
     */
    fun getUserContactsWithRequests(): Flow<List<UserContactDTO>> {
        return userDao.getContactsNotOfRelationFlow(FRIENDS)
            .map {
                it
                    .filter { contact -> contact.syncState != SyncState.PENDING_REMOVAL }
                    .map((UserContactMapper::toDTO)(context))
                    .take(context.resources.getInteger(R.integer.max_contact_requests_to_display))
            }
            .combine(getUserContacts()) { x, y -> x + y }
    }

    /**
     * Gets the list of user contacts to display
     *
     * @return A flow containing the list of contacts
     */
    fun getUserContacts(): Flow<List<UserContactDTO>> {
        return userDao.getContactsOfRelationFlow(FRIENDS).map { userContacts ->
            userContacts
                .filter { it.syncState != SyncState.PENDING_REMOVAL }
                .map((UserContactMapper::toDTO)(context))
        }
    }

    /**
     * Log user out
     */
    suspend fun logUserOut(): Result<Unit> {
        return performAction(dispatcher) {
            sharedPreferenceDao.clearAuthenticationUser()
            appDatabase.clearAllTables()
        }
    }

    /**
     * Database
     */

    /**
     * Updates the local user from the user profile update form input
     *
     * @param updateUserInput The update user form input
     */
    suspend fun updateUser(updateUserInput: UpdateUserInput): Result<Unit> =
        performAction(dispatcher) {
            sharedPreferenceDao.getAuthUserRemoteId()
                ?.let { userDao.getUser(it) }
                ?.let(UpdateUserUpdateMapper.toLocalUpdate(updateUserInput))
                ?.let { userDao.update(it) }
        }

    /**
     * Submits a user contact for later deletion through an API request
     *
     * @param contactRemoteId The remote id of the contact to submit for deletion
     */
    suspend fun submitContactForDeletion(contactRemoteId: String): Result<Unit> =
        performAction(dispatcher) {
            userDao.getContact(contactRemoteId)?.let {
                userDao.update(it.copy(syncState = SyncState.PENDING_REMOVAL))
            }
        }


    /**
     * Updates local user with the response received from the API
     *
     * @param userResponse The API response
     */
    private suspend fun updateUserFromResponse(userResponse: UserResponse) {
        val pendingUser = userDao.getUserBySyncState(userResponse.remoteId, SyncState.PENDING_UPDATE)
        pendingUser ?: userDao.insert(UserMapper.toEntity(userResponse))
    }

    /**
     * Updates local user contacts with the response received from the API
     *
     * @param contactsResponse The API response
     */
    private suspend fun updateUserContactsFromResponse(contactsResponse: UserContactListResponse) {
        val pendingContacts = userDao.getContactIdsByNotSyncState(SyncState.UP_TO_DATE)
        val newContacts = UserContactMapper.toEntity(contactsResponse).filter { !pendingContacts.contains(it.remoteId) }
        userDao.clearContacts(
            (contactsResponse.contacts + contactsResponse.receivedRequests + contactsResponse.sentRequests)
                .map(UserContactResponse::remoteId)
        )
        userDao.insert(newContacts)
    }

    /**
     * Updates the local user settings with the response received from the API
     *
     * @param userSettingsResponse The API response
     */
    private suspend fun updateUserSettingsFromResponse(userSettingsResponse: UserSettingsResponse) {
        sharedPreferenceDao.updateDefaultSharedPreferences(
            UserSettingMapper.toEntity(userSettingsResponse)
        )
    }

    /**
     * Network
     */

    /**
     * Unauthenticated requests
     */

    /**
     * Log user in
     *
     * @param logInUserInput The log in form input
     */
    suspend fun logUserIn(logInUserInput: LogInUserInput): Result<Unit> =
        performAction(dispatcher) {
            val deviceId = sharedPreferenceDao.getUniqueDeviceId()
            val request = LogInUserRequest(logInUserInput.id, logInUserInput.password, deviceId)
            val loginResponse: UserLoginResponse = userApiService.logUserIn(request)
            sharedPreferenceDao.updateAuthenticatedUserPrefs(
                loginResponse.authToken,
                loginResponse.user.remoteId,
                logInUserInput.rememberMe
            )
            updateUserFromResponse(loginResponse.user)
            updateUserSettingsFromResponse(loginResponse.settings)
        }

    /**
     * Sign a user up
     *
     * @param createUserInput The sign up form input
     */
    suspend fun signUserUp(createUserInput: CreateUserInput): Result<Unit> =
        performAction(dispatcher) {
            val request = CreateUserMapper.toNetworkRequest(createUserInput)
            userApiService.signUserUp(request)
            logUserIn(LogInUserInput(createUserInput.username, createUserInput.password, true))
        }

    /**
     * Authenticated requests
     */

    /**
     * Create
     */

    /**
     * Creates a contact request
     *
     * @param remoteId The remote id of the user to send the request to
     * @return A success or failure result
     */
    suspend fun createContactRequest(remoteId: String): Result<Unit> =
        performAuthenticatedAction(dispatcher) { authHeader ->
            val request = CreateContactRequest(remoteId)
            userApiService.createUserContactRequest(authHeader, request)
        }

    /**
     * Read
     */

    /**
     * Retrieves the locally authenticated user data from the API and saves them to the local database
     *
     * @return A success or failure result
     */
    suspend fun retrieveAuthenticatedUserData(): Result<Unit> =
        performAuthenticatedAction(dispatcher) { authHeader ->
            if (!sharedPreferenceDao.shouldRememberMe())
                sharedPreferenceDao.clearAuthenticationUser()
            val response = userApiService.getAuthenticatedUser(authHeader)
            updateUserFromResponse(response.user)
            updateUserSettingsFromResponse(response.settings)
        }

    /**
     * Retrieves the local user user contacts from the API and saves them to the local database
     *
     * @return A success or failure result
     */
    suspend fun retrieveUserContacts(): Result<Unit> =
        performAuthenticatedAction(dispatcher) { authHeader ->
            val contactsResponse = userApiService.getUserContacts(authHeader)
            updateUserContactsFromResponse(contactsResponse)
        }

    /**
     * Retrieve the local user contact search results from the API and saves them to the local database
     *
     * @param searchTag
     * @return A success result containing the list of found users or failure result
     */
    suspend fun retrieveUserContactSearchResults(searchTag: String): Result<List<UserContactDTO>> {
        return performAuthenticatedAction(dispatcher) { authHeader ->
            val foundContacts = userApiService.searchContacts(authHeader, searchTag)
            UserContactMapper.toSearchContactResult(context)(foundContacts)
        }
    }

    /**
     * Update
     */

    /**
     * Sends an user request to the API for the locally updated user
     *
     * @return A success or failure result
     */
    suspend fun sendUserUpdate(): Result<Unit> {
        return performAuthenticatedAction(dispatcher) { authHeader ->
            val authenticatedUser: User? = sharedPreferenceDao.getAuthUserRemoteId()?.let { userDao.getUserBySyncState(it, SyncState.PENDING_UPDATE) }
            authenticatedUser?.let { user ->
                val request: UpdateUserRequest = UpdateUserUpdateMapper.toNetworkRequest(user)
                userApiService.updateAuthenticatedUser(authHeader, request)
                userDao.update(user.copy(syncState = SyncState.UP_TO_DATE))
            }
        }
    }

    /**
     * Sends an user settings request to the API for the locally updated user settings
     *
     * @return A success or failure result
     */
    suspend fun sendUserSettingsUpdate(): Result<Unit> =
        performAuthenticatedAction(dispatcher) { authHeader ->
            val request: UpdateUserSettingsRequest = UserSettingMapper.toNetworkRequest(sharedPreferenceDao.getUserSettings())
            userApiService.updateUserSettings(authHeader, request)
        }

    /**
     * Delete
     */

    /**
     * Sends a user contacts delete request to the api for the locally deleted comments
     *
     * @return A success or failure result
     */
    suspend fun sendDeleteContactRequests(): Result<Unit> =
        performAuthenticatedAction(dispatcher) { authHeader ->
            val contactsToRemove: List<UserContact> =
                userDao.getContactsBySyncState(SyncState.PENDING_REMOVAL)
            val request = contactsToRemove.mapNotNull {
                when (it.relationType) {
                    INCOMING -> RemoveContactIdRequest(senderRemoteId = it.remoteId)
                    OUTGOING -> RemoveContactIdRequest(receiverRemoteId = it.remoteId)
                    FRIENDS -> RemoveContactIdRequest(contactRemoteId = it.remoteId)
                    NONE -> null
                }
            }
            userApiService.deleteUserContact(authHeader, request)
            userDao.deleteContacts(contactsToRemove)
        }
}

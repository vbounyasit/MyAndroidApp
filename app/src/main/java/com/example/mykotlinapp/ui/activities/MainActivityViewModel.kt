package com.example.mykotlinapp.ui.activities

import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.mykotlinapp.SOCKET_CHAT_CHANGED_EVENT
import com.example.mykotlinapp.SOCKET_CHAT_NEW_MESSAGE_EVENT
import com.example.mykotlinapp.SOCKET_CHAT_READ_EVENT
import com.example.mykotlinapp.model.dto.ui.user.UserDTO
import com.example.mykotlinapp.model.repository.impl.ChatRepository
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.network.socket.SocketComponent
import com.example.mykotlinapp.network.socket.SocketEventListener
import com.example.mykotlinapp.network.socket.dto.ChatReadMessage
import com.example.mykotlinapp.network.socket.dto.NewEntitiesMessage
import com.example.mykotlinapp.ui.AppViewModel
import com.example.mykotlinapp.ui.components.DialogFormFragmentManager
import com.example.mykotlinapp.ui.components.SharedPreferenceLiveData.Companion.SharedPreferenceBooleanLiveData
import com.example.mykotlinapp.ui.components.drawer.BottomDrawerManager
import com.example.mykotlinapp.ui.components.notifications.NotificationComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val socketComponent: SocketComponent,
    val workManager: WorkManager,
    val notificationComponent: NotificationComponent,
    val bottomDrawerManager: BottomDrawerManager,
    val dialogFormFragmentManager: DialogFormFragmentManager,
) : AppViewModel() {

    private val _isBottomNavVisible = MutableLiveData<Boolean>()
    private val _isLoggedOut = MutableLiveData<Boolean>()
    private val _openUserDrawer = MutableLiveData<Boolean>()
    private val _darkMode = userRepository.newDarkModeLiveData()
    private val _bottomNavigationDestination = MutableLiveData<Int?>()
    private val _navigateToChatRemoteId = MutableLiveData<String?>()

    private val _userRemoteId = userRepository.newUserRemoteIdLiveData()

    val isBottomNavVisible: LiveData<Boolean> = _isBottomNavVisible
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut
    val openUserDrawer: LiveData<Boolean> = _openUserDrawer
    val darkMode: SharedPreferenceBooleanLiveData = _darkMode
    val bottomNavigationDestination: LiveData<Int?> = _bottomNavigationDestination
    val navigateToChatRemoteId: LiveData<String?> = _navigateToChatRemoteId

    val userData: LiveData<UserDTO?> = _userRemoteId.switchMap {
        userRepository.getUserData(it).asLiveData()
    }

    fun retrieveInitialData() = submitHttpRequest { userRepository.retrieveUserContacts() }

    fun toggleViewMode(darkMode: Boolean) {
        viewModelScope.launch {
            userRepository.updateDarkMode(darkMode)
        }
    }

    fun clearUserData() {
        viewModelScope.launch { userRepository.logUserOut() }
    }

    fun setBottomNavigationDestination(fragmentId: Int?) {
        _bottomNavigationDestination.value = fragmentId
    }

    fun onBottomNavigation() {
        _bottomNavigationDestination.value = null
    }

    fun setNavigationToChatRemoteId(chatRemoteId: String) {
        _navigateToChatRemoteId.value = chatRemoteId
    }

    fun onNavigatedToChatRemoteId() {
        _navigateToChatRemoteId.value = null
    }

    fun logUserOut() {
        _isLoggedOut.value = true
    }

    fun openUserDrawer() {
        _openUserDrawer.value = true
    }

    fun onLogUserOut() {
        _isLoggedOut.value = false
    }

    fun onOpenUserDrawer() {
        _openUserDrawer.value = false
    }

    fun hideBottomNav() {
        _isBottomNavVisible.value = false
    }

    fun showBottomNav() {
        _isBottomNavVisible.value = true
    }

    fun cancelBackgroundWork() {
        workManager.cancelAllWork()
    }

    /**
     * Socket
     */

    fun initializeAndConnectSocket() = viewModelScope.launch {
        socketComponent.initialize()
        socketComponent.connect()
        listenToSocketEvents()
    }

    private fun listenToSocketEvents() {
        socketComponent.addEvent(
            SOCKET_CHAT_READ_EVENT,
            SocketEventListener<ChatReadMessage> { data ->
                viewModelScope.launch { chatRepository.updateChatParticipantReadTime(data) }
            })
        socketComponent.addEvent(
            SOCKET_CHAT_NEW_MESSAGE_EVENT,
            SocketEventListener<NewEntitiesMessage> { data ->
                viewModelScope.launch {
                    chatRepository.retrieveNewChatLogs(data.remoteIds)
                    chatRepository.getChatLogsAppNotifications(data.remoteIds)?.forEach {
                        notificationComponent.sendChatNotification(it)
                    }
                }
            })
        socketComponent.addEvent(
            SOCKET_CHAT_CHANGED_EVENT,
            SocketEventListener<NewEntitiesMessage> { data ->
                viewModelScope.launch {
                    chatRepository.retrieveNewChatNotifications(data.remoteIds)
                }
            })
    }

    fun disconnectSocket() = viewModelScope.launch {
        socketComponent.disconnect()
    }
}
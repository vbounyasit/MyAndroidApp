package com.example.mykotlinapp.ui.screens.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.ui.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomePageViewModel @Inject constructor(private val userRepository: UserRepository) : AppViewModel() {

    private val _navigatingToUserPage = MutableLiveData<Boolean>()
    private val _requestedLogIn = MutableLiveData<Boolean>()

    val navigatingToUserPage: LiveData<Boolean> = _navigatingToUserPage
    val requestedLogIn: LiveData<Boolean> = _requestedLogIn

    fun handleNextStep() {
        viewModelScope.launch {
            delay(1000)
            if (userRepository.getUserAuthToken() != null)
                _requestedLogIn.value = true
            else {
                navigateToUserPage()
            }
        }
    }

    fun navigateToUserPage() {
        _navigatingToUserPage.value = true
    }

    fun onRequestLogin() {
        _requestedLogIn.value = false
    }

    fun onNavigatedToUserPage() {
        _navigatingToUserPage.value = false
    }
}
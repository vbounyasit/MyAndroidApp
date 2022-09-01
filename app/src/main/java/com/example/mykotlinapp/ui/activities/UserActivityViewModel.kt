package com.example.mykotlinapp.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykotlinapp.domain.pojo.ApiRequestState
import com.example.mykotlinapp.model.dto.inputs.form.user.CreateUserInput
import com.example.mykotlinapp.model.dto.inputs.form.user.LogInUserInput
import com.example.mykotlinapp.model.repository.impl.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

@HiltViewModel
class UserActivityViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {

    private val _loginState = MutableLiveData<ApiRequestState>()
    private val _signUpState = MutableLiveData<ApiRequestState>()

    val loginState: LiveData<ApiRequestState> = _loginState
    val signUpState: LiveData<ApiRequestState> = _signUpState

    fun signUp(createUserInput: CreateUserInput) {
        _signUpState.value = ApiRequestState.LOADING
        viewModelScope.launch {
            userRepository.signUserUp(createUserInput)
                .onSuccess { _signUpState.value = ApiRequestState.SUCCESS }
                .onFailure { _signUpState.value = ApiRequestState.FAILED }
        }
    }

    fun logIn(logInUserInput: LogInUserInput? = null) {
        _loginState.value = ApiRequestState.LOADING
        viewModelScope.launch {
            val loginResult: Result<Unit> = logInUserInput?.let { userRepository.logUserIn(it) } ?: userRepository.retrieveAuthenticatedUserData()

            loginResult
                .onSuccess { _loginState.value = ApiRequestState.SUCCESS }
                .onFailure { exception ->
                    if (exception is HttpException && exception.code() == HttpsURLConnection.HTTP_UNAUTHORIZED)
                        _loginState.value = ApiRequestState.FAILED_UNAUTHORIZED
                    else
                        _loginState.value = ApiRequestState.FAILED
                }
        }
    }

    fun onSignUp() {
        _signUpState.value = ApiRequestState.FINISHED
    }

    fun onLogIn() {
        _loginState.value = ApiRequestState.FINISHED
    }

    suspend fun isDarkMode() = userRepository.isDarkMode()
}
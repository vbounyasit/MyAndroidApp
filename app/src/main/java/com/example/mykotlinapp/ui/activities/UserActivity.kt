package com.example.mykotlinapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.ActivityUserBinding
import com.example.mykotlinapp.domain.pojo.ApiRequestState
import com.example.mykotlinapp.ui.AppActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserActivity : AppActivity() {

    private lateinit var binding: ActivityUserBinding
    private val viewModel by viewModels<UserActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
        updateNightMode()
    }

    override fun registerObservers() {
        fun startOnSuccess(action: () -> Unit): (ApiRequestState) -> Unit = {
            if(it == ApiRequestState.SUCCESS)
                startMainActivity(action)
        }
        viewModel.loginState.observe(this, ::startOnSuccess { viewModel.onLogIn() })
        viewModel.signUpState.observe(this, ::startOnSuccess { viewModel.onSignUp() })
    }

    private fun updateNightMode() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                setDefaultNightMode(if (viewModel.isDarkMode()) MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun startMainActivity(onFinish: () -> Unit) {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
        onFinish()
    }

}
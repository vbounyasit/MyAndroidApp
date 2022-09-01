package com.example.mykotlinapp.ui.screens.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentWelcomePageBinding
import com.example.mykotlinapp.domain.pojo.ApiRequestState
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.activities.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomePageFragment : AppFragment() {

    private val viewModel by viewModels<WelcomePageViewModel>()
    private val sharedViewModel: UserActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentWelcomePageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomePageBinding.inflate(inflater)
        binding.lifecycleOwner = requireActivity()
        binding.activityViewModel = sharedViewModel
        if (!skipWelcomePage())
            viewModel.handleNextStep()
        return binding.root
    }

    override fun registerObservers() {
        super.registerObservers()
        sharedViewModel.loginState.observe(viewLifecycleOwner) {
            if (it == ApiRequestState.FAILED) viewModel.navigateToUserPage()
        }
        viewModel.requestedLogIn.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.logIn()
                viewModel.onRequestLogin()
            }
        }
        viewModel.navigatingToUserPage.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(WelcomePageFragmentDirections.actionWelcomePageFragmentToUserPageFragment())
                viewModel.onNavigatedToUserPage()
            }
        }
    }


    private fun skipWelcomePage(): Boolean {
        //skip welcome page if user logged out
        val shouldSkip = requireActivity().intent.getBooleanExtra(getString(R.string.skip_welcome_page), false)
        if (shouldSkip) viewModel.navigateToUserPage()
        return shouldSkip
    }

}
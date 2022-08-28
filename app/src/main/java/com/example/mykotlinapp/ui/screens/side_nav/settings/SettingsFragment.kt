package com.example.mykotlinapp.ui.screens.side_nav.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.mykotlinapp.databinding.FragmentSettingsBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.activities.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : AppFragment() {

    private val viewModel by viewModels<SettingsViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        sharedViewModel.hideBottomNav()
        return binding.root
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.settingsTopBar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onDestroy() {
        viewModel.launchUserSettingsWork()
        sharedViewModel.showBottomNav()
        super.onDestroy()
    }
}
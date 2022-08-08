package com.example.mykotlinapp.ui.screens.community.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mykotlinapp.databinding.FragmentTabChannelsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChannelFragment : Fragment() {

    private val viewModel by viewModels<ChannelViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentTabChannelsBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}
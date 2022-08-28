package com.example.mykotlinapp.ui.screens.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mykotlinapp.databinding.FragmentMainDiscoverBinding
import com.example.mykotlinapp.ui.activities.MainActivityViewModel

class DiscoverFragment : Fragment() {

    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentMainDiscoverBinding.inflate(inflater)
        binding.discoverTopBar.setNavigationOnClickListener { sharedViewModel.openUserDrawer() }
        return binding.root
    }

}
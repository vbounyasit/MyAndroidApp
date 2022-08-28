package com.example.mykotlinapp.ui.screens.chats.history.group.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentTabGroupEventsBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupEventsFragment : AppFragment() {

    private val viewModel by viewModels<GroupEventsViewModel>()

    private val adapter by lazy {
        GroupEventsAdapter(
            ClickListener { viewModel.onEventClicked(it) },
            requireContext().resources.getDimension(R.dimen.event_participant_pic_size).toInt()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentTabGroupEventsBinding.inflate(inflater)
        binding.viewModel = viewModel
        //viewModel.loadEvents(groupRemoteId)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.groupEventsList.adapter = adapter
        return binding.root
    }
}
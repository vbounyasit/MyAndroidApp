package com.example.mykotlinapp.ui.screens.chats.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mykotlinapp.activities.MainActivityViewModel
import com.example.mykotlinapp.databinding.FragmentTabChatListBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import com.example.mykotlinapp.ui.screens.chats.ChatFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatHistoryFragment : AppFragment() {

    private val viewModel by viewModels<ChatHistoryViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private val adapter: ChatListAdapter by lazy {
        ChatListAdapter(ClickListener { viewModel.onChatClicked(it.remoteId) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentTabChatListBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.groupChatList.itemsRecyclerView.adapter = adapter
        viewModel.retrieveData()
        return binding.root
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.navigateToGroupChat.observe(viewLifecycleOwner) { chatRemoteId ->
            chatRemoteId?.let {
                findNavController().navigate(ChatFragmentDirections.actionChatFragmentToGroupChatFragment(it))
                viewModel.onNavigatedToGroupChat()
            }
        }
        sharedViewModel.navigateToChatRemoteId.observe(viewLifecycleOwner) {
            it?.let { remoteId ->
                viewModel.onChatClicked(remoteId)
                sharedViewModel.onNavigatedToChatRemoteId()
            }
        }
    }

}
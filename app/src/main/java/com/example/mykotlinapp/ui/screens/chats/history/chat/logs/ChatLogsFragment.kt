package com.example.mykotlinapp.ui.screens.chats.history.chat.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentChatLogsBinding
import com.example.mykotlinapp.ui.AppFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatLogsFragment : AppFragment() {

    private val viewModel by viewModels<ChatLogsViewModel>()

    private lateinit var binding: FragmentChatLogsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatLogsBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        arguments?.getBoolean(getString(R.string.is_group_chat))?.let {
            binding.chatBubbleList.adapter = ChatLogsAdapter(
                it,
                requireContext().resources.getDimension(R.dimen.chat_participant_read_pic_size)
                    .toInt()
            )
        }
        arguments?.getString(getString(R.string.chat_remote_id))?.let {
            viewModel.updateChatRemoteId(it)
        }
        return binding.root
    }

    override fun registerObservers() {
        super.registerObservers()
        binding.chatBubbleList.adapter?.let {
            it.registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    binding.chatBubbleList.layoutManager?.smoothScrollToPosition(
                        binding.chatBubbleList,
                        null,
                        it.itemCount
                    )
                    viewModel.readChat()
                }
            })
        }
        binding.chatMessageToolbar.chatMessageInputField.setOnEditorActionListener { view, _, _ ->
            val input = view.text.toString().trim()
            if (input.isNotBlank()) {
                viewModel.createChatLog(input)
                binding.chatMessageToolbar.chatMessageInputField.text?.clear()
                false
            } else true
        }
        binding.chatMessageToolbar.chatSend.setOnClickListener {
            binding.chatMessageToolbar.chatMessageInputField.text?.let { editable ->
                val text = editable.toString().trim()
                if (text.isNotBlank()) {
                    viewModel.createChatLog(text)
                    editable.clear()
                }
            }
        }
    }

    override fun onResume() {
        viewModel.readChat()
        super.onResume()
    }
}
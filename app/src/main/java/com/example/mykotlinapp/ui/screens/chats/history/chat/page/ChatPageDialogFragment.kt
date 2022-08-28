package com.example.mykotlinapp.ui.screens.chats.history.chat.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mykotlinapp.databinding.DialogChatPageBinding
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import com.example.mykotlinapp.ui.screens.chats.contacts.ContactInfoDialogFragment
import com.example.mykotlinapp.ui.screens.chats.contacts.search.ContactSearchListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatPageDialogFragment(val chatRemoteId: String) : DialogFragment() {

    private val viewModel by viewModels<ChatPageDialogViewModel>()

    private val adapter by lazy {
        ContactSearchListAdapter(
            listener = ClickListener {
                ContactInfoDialogFragment(it)
                    .show(childFragmentManager, ContactInfoDialogFragment.TAG)
            },
            sendRequestListener = ClickListener { viewModel.sendContactRequest(it.remoteId) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogChatPageBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.updateChatRemoteId(chatRemoteId)
        binding.contactParticipants.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    companion object {
        const val TAG = "chatPage"
    }
}
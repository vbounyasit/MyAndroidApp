package com.example.mykotlinapp.ui.screens.chats.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mykotlinapp.databinding.FragmentTabContactListBinding
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactFragment : AppFragment() {

    private val viewModel by viewModels<ContactViewModel>()

    private val contactAdapter by lazy {
        ContactListAdapter(
            listener = ClickListener {
                ContactInfoDialogFragment(it) { viewModel.removeContactOrRequest(it.remoteId) }
                    .show(childFragmentManager, "contactInfo")
            },
            acceptListener = ClickListener {
                if (it.relationType == ContactRelationType.OUTGOING)
                    viewModel.removeContactOrRequest(it.remoteId)
                else
                    viewModel.acceptRequest(it.remoteId)
            },
            declineListener = ClickListener { viewModel.removeContactOrRequest(it.remoteId) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTabContactListBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.contactList.itemsRecyclerView.adapter = contactAdapter
        viewModel.retrieveData()
        return binding.root
    }
}
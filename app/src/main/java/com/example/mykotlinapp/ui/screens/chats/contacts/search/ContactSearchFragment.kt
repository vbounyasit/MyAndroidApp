package com.example.mykotlinapp.ui.screens.chats.contacts.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import com.example.mykotlinapp.WORK_CREATE_CONTACT_INPUT_KEY
import com.example.mykotlinapp.databinding.FragmentSearchPageBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.activities.MainActivityViewModel
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import com.example.mykotlinapp.ui.screens.chats.contacts.ContactInfoDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ContactSearchFragment : AppFragment() {

    private val viewModel by viewModels<ContactSearchViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentSearchPageBinding

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
        binding = FragmentSearchPageBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.contactSearchResult.itemsRecyclerView.adapter = adapter
        sharedViewModel.hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showSoftKeyboard(binding.contactSearchInputField)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.contactSearchTopAppBar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.contactSearchInputField.setOnEditorActionListener { v, _, _ ->
            val input = v.text.toString()
            if (input.isNotBlank()) {
                viewModel.updateSearchResults(input.trim())
                false
            } else true
        }
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.createContactWorkInfo.observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && it.first().state.isFinished && it.first().state == WorkInfo.State.SUCCEEDED) {
                it.first().outputData.getString(WORK_CREATE_CONTACT_INPUT_KEY)?.let { remoteId ->
                    viewModel.setOutgoingRequest(remoteId)
                }
            }
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.showBottomNav()
    }
}
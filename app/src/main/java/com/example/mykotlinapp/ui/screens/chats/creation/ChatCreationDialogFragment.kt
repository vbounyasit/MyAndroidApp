package com.example.mykotlinapp.ui.screens.chats.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogCreateChatBinding
import com.example.mykotlinapp.model.dto.inputs.form.chat.CreateChatInput
import com.example.mykotlinapp.ui.DialogFormFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.MaxLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.MinLengthValidator
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import com.example.mykotlinapp.ui.screens.chats.creation.ListAdapters.ContactSelectionListAdapter
import com.example.mykotlinapp.ui.screens.chats.creation.ListAdapters.ContactSelectorListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class ChatCreationDialogFragment(confirmFunction: (CreateChatInput) -> Job) :
    DialogFormFragment<CreateChatInput>(confirmFunction) {

    private lateinit var binding: DialogCreateChatBinding
    private val viewModel by viewModels<ChatCreationViewModel>()

    override val dialogFragmentTag: String = "ChatCreationDialog"

    override val cancelAlert: CancelAlert by lazy {
        CancelAlert(
            title = getString(R.string.discard_changes_alert),
            message = getString(R.string.discard_changes_alert_message),
            confirmLabel = getString(R.string.discard_changes_alert_label)
        )
    }

    private val contactSelectionAdapter =
        ContactSelectionListAdapter(ClickListener {
            viewModel.selectedContacts.value?.let { selectedContacts ->
                if (selectedContacts.size > 1 || viewModel.onMessagePage.value == false)
                    viewModel.updateContactSelection(it, false)
                else
                    Toast.makeText(
                        requireContext(),
                        R.string.error_no_participants_selected,
                        Toast.LENGTH_SHORT
                    ).show()
            }
        })

    private val contactSelectorAdapter =
        ContactSelectorListAdapter { contact, selected ->
            viewModel.updateContactSelection(
                contact,
                selected
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreateChatBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.selectionContactList.adapter = contactSelectionAdapter
        binding.selectorContactList.adapter = contactSelectorAdapter
        cancelButton = binding.chatCreationDialogOptions.dialogCancelButton
        submitButton = binding.chatCreationMessageInput.chatSend
        registerListeners()
        return binding.root
    }

    private fun registerListeners() {
        binding.chatCreationDialogOptions.dialogSubmitButton.setOnClickListener {
            if (!viewModel.hasSelectedContacts())
                Toast.makeText(
                    requireContext(),
                    R.string.error_no_participants_selected,
                    Toast.LENGTH_SHORT
                ).show()
            else
                viewModel.toggleOnMessagePage()
        }
        binding.contactSelectionSearchBarField.doAfterTextChanged {
            it?.let { editable -> viewModel.updateSearchTag(editable.toString()) }
        }
        binding.chatCreationMessageInput.chatMessageInputField.setOnEditorActionListener { v, _, _ ->
            if (v.text.toString().isNotBlank()) {
                getFormValidator().performSubmitAction()
                false
            } else {
                Toast.makeText(requireContext(), R.string.error_message_empty, Toast.LENGTH_LONG)
                true
            }
        }
    }

    override fun getFormValidator(): FormValidator<CreateChatInput> {
        val groupName = binding.chatCreationChatNameInputField
        val chatMessage = binding.chatCreationMessageInput.chatMessageInputField
        return FormValidator(
            requireContext(),
            binding.root,
            viewModel.selectedContacts.value?.let {
                CreateChatInput(
                    groupName.getContent().ifBlank { null },
                    chatMessage.getContent(),
                    it.map { contact -> contact.remoteId }
                )
            },
            TextInputFieldValidator(
                groupName,
                MinLengthValidator(resources.getInteger(R.integer.input_field_min_length)),
                MaxLengthValidator(resources.getInteger(R.integer.input_field_max_length)),
            ),
            submitAction = { input ->
                binding.chatCreationMessageInput.chatMessageInputField.text.toString().let {
                    if (it.isNotBlank())
                        submit(input)
                }
            }
        )
    }
}
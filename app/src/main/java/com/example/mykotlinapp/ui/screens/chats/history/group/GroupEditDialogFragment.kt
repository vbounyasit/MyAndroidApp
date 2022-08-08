package com.example.mykotlinapp.ui.screens.chats.history.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogEditGroupBinding
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateGroupInput
import com.example.mykotlinapp.ui.DialogFormFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.GroupNameValidator
import com.example.mykotlinapp.ui.components.form.validators.MaxLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.MinLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.ValueRequiredValidator
import kotlinx.coroutines.Job

class GroupEditDialogFragment(
    confirmFunction: (UpdateGroupInput) -> Job,
) : DialogFormFragment<UpdateGroupInput>(confirmFunction) {

    override val isFullScreen: Boolean = false
    override val dialogFragmentTag: String = "GroupEditDialogFragment"

    private lateinit var binding: DialogEditGroupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogEditGroupBinding.inflate(inflater)
        binding.groupData = initialInput
        cancelButton = binding.editGroupDialogOptions.dialogCancelButton
        submitButton = binding.editGroupDialogOptions.dialogSubmitButton
        return binding.root
    }

    override fun getFormValidator(): FormValidator<UpdateGroupInput> {
        val name = binding.editGroupNameField
        val description = binding.editGroupDescriptionField
        return FormValidator(
            requireContext(),
            binding.root,
            initialInput?.let {
                UpdateGroupInput(
                    it.remoteId,
                    name.getContent().ifBlank { null },
                    description.getContent().ifBlank { null }
                )
            },
            TextInputFieldValidator(
                name,
                ValueRequiredValidator,
                MinLengthValidator(resources.getInteger(R.integer.input_field_min_length)),
                MaxLengthValidator(resources.getInteger(R.integer.input_field_max_length)),
                GroupNameValidator
            ),
            TextInputFieldValidator(description, MaxLengthValidator(resources.getInteger(R.integer.description_max_length))),
            submitAction = { submit(it) }
        )
    }

}
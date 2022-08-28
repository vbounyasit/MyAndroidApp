package com.example.mykotlinapp.ui.screens.side_nav.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogEditUserProfileBinding
import com.example.mykotlinapp.model.dto.inputs.form.user.UpdateUserInput
import com.example.mykotlinapp.ui.DialogFormFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.*
import kotlinx.coroutines.Job

class UserProfileEditDialogFragment(
    confirmFunction: (UpdateUserInput) -> Job,
) : DialogFormFragment<UpdateUserInput>(confirmFunction) {

    private lateinit var binding: DialogEditUserProfileBinding

    override val dialogFragmentTag: String = "UserProfileEditDialogFragment"


    override val cancelAlert: CancelAlert by lazy {
        CancelAlert(
            title = getString(R.string.discard_changes_alert),
            message = getString(R.string.discard_changes_alert_message),
            confirmLabel = resources.getString(R.string.discard_changes_alert_label)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditUserProfileBinding.inflate(inflater)
        binding.userData = initialInput
        cancelButton = binding.editProfileDialogOptions.dialogCancelButton
        submitButton = binding.editProfileDialogOptions.dialogSubmitButton
        return binding.root
    }

    override fun getFormValidator(): FormValidator<UpdateUserInput> {
        val lengthValidators = arrayOf(
            ValueRequiredValidator,
            MinLengthValidator(resources.getInteger(R.integer.input_field_min_length)),
            MaxLengthValidator(resources.getInteger(R.integer.input_field_max_length))
        )
        val nameFieldValidator = lengthValidators + NameValidator
        val firstName = binding.editProfileFirstNameField
        val lastName = binding.editProfileLastNameField
        val email = binding.editProfileEmailField
        val aboutMe = binding.editProfileAboutMeField
        return FormValidator(
            requireContext(),
            binding.root,
            UpdateUserInput(
                firstName.getContent(),
                lastName.getContent(),
                email.getContent(),
                aboutMe.getContent().ifBlank { null }
            ),
            TextInputFieldValidator(email, *lengthValidators, EmailValidator),
            TextInputFieldValidator(firstName, *nameFieldValidator),
            TextInputFieldValidator(lastName, *nameFieldValidator),
            submitAction = { submit(it) }
        )
    }
}
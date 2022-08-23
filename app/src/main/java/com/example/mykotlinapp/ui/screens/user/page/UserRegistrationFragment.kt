package com.example.mykotlinapp.ui.screens.user.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.mykotlinapp.R
import com.example.mykotlinapp.ui.activities.UserActivityViewModel
import com.example.mykotlinapp.databinding.FragmentTabUserSignupBinding
import com.example.mykotlinapp.domain.pojo.Gender
import com.example.mykotlinapp.model.dto.inputs.form.user.CreateUserInput
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.components.DatePickerDialogFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.RadioGroupFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserRegistrationFragment : AppFragment() {

    private val sharedViewModel: UserActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentTabUserSignupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTabUserSignupBinding.inflate(inflater)
        binding.lifecycleOwner = requireActivity()
        binding.activityViewModel = sharedViewModel
        return binding.root
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.inputBirthdayField.setOnClickListener {
            val newFragment = DatePickerDialogFragment(binding.inputBirthdayField)
            newFragment.show(parentFragmentManager, "datePicker")
        }
        binding.signupButton.setOnClickListener { getFormValidator().performSubmitAction() }
    }

    private fun getFormValidator(): FormValidator<CreateUserInput> {
        val lengthValidators = arrayOf(
            ValueRequiredValidator,
            MinLengthValidator(resources.getInteger(R.integer.input_field_min_length)),
            MaxLengthValidator(resources.getInteger(R.integer.input_field_max_length))
        )
        val nameFieldValidator = lengthValidators + NameValidator
        val email = binding.inputCreateEmailField
        val username = binding.inputCreateUsernameField
        val firstName = binding.inputCreateFirstNameField
        val lastName = binding.inputCreateLastNameField
        val password = binding.inputCreatePasswordField
        val birthDay = binding.inputBirthdayField
        val gender = binding.genderRadioGroup
        val resultGender = if (gender.checkedRadioButtonId == R.id.choice_male_gender) Gender.MALE else Gender.FEMALE

        return FormValidator(
            requireContext(),
            binding.root,
            CreateUserInput(
                email.getContent(),
                username.getContent(),
                firstName.getContent(),
                lastName.getContent(),
                password.getContent(),
                birthDay.getContent(),
                resultGender
            ),
            TextInputFieldValidator(email, *lengthValidators, EmailValidator, clearOnSubmit = false),
            TextInputFieldValidator(username, *nameFieldValidator, clearOnSubmit = false),
            TextInputFieldValidator(firstName, *nameFieldValidator, clearOnSubmit = false),
            TextInputFieldValidator(lastName, *nameFieldValidator, clearOnSubmit = false),
            TextInputFieldValidator(password, *lengthValidators, clearOnSubmit = false),
            TextInputFieldValidator(birthDay, ValueRequiredValidator, clearOnSubmit = false),
            RadioGroupFieldValidator(requireContext(), gender),
            submitAction = sharedViewModel::signUp
        )
    }

}
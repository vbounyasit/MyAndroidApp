package com.example.mykotlinapp.ui.screens.user.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentTabUserLoginBinding
import com.example.mykotlinapp.model.dto.inputs.form.user.LogInUserInput
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.activities.UserActivityViewModel
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.MaxLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.MinLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.NameValidator
import com.example.mykotlinapp.ui.components.form.validators.ValueRequiredValidator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserLoginFragment : AppFragment() {

    private val sharedViewModel: UserActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentTabUserLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabUserLoginBinding.inflate(inflater)
        binding.activityViewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.loginButton.setOnClickListener { getFormValidator().performSubmitAction() }
    }

    private fun getFormValidator(): FormValidator<LogInUserInput> {
        val idField = binding.inputUsernameField
        val passwordField = binding.inputPasswordField
        val rememberMe = binding.rememberMeSwitch.isChecked
        val lengthValidators = arrayOf(
            ValueRequiredValidator,
            MinLengthValidator(resources.getInteger(R.integer.input_field_min_length)),
            MaxLengthValidator(resources.getInteger(R.integer.input_field_max_length)),
            NameValidator
        )
        return FormValidator(
            requireContext(),
            binding.root,
            LogInUserInput(idField.getContent(), passwordField.getContent(), rememberMe),
            TextInputFieldValidator(idField, *lengthValidators, clearOnSubmit = false),
            TextInputFieldValidator(passwordField, *lengthValidators, clearOnSubmit = false),
            submitAction = sharedViewModel::logIn
        )
    }

}
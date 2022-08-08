package com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogEditPostBinding
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostInput
import com.example.mykotlinapp.ui.BottomSheetFormFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.MaxLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.ValueRequiredValidator
import kotlinx.coroutines.Job

class PostEditSheetFragment(
    confirmFunction: (UpdatePostInput) -> Job,
) : BottomSheetFormFragment<UpdatePostInput>(confirmFunction) {

    override val dialogFragmentTag: String = "PostEditDialogFragment"

    private lateinit var binding: DialogEditPostBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogEditPostBinding.inflate(inflater)
        binding.postData = initialInput
        setUpListeners()
        return binding.root
    }

    private fun setUpListeners() {
        binding.editPostDialogOptions.dialogCancelButton.setOnClickListener { dismiss() }
        binding.editPostDialogOptions.dialogSubmitButton.setOnClickListener { getFormValidator().performSubmitAction() }
    }

    override fun getFormValidator(): FormValidator<UpdatePostInput> {
        val content = binding.editPostContentField
        return FormValidator(
            requireContext(),
            binding.root,
            initialInput?.let { UpdatePostInput(it.remoteId, content.getContent()) },
            TextInputFieldValidator(content,
                ValueRequiredValidator,
                MaxLengthValidator(resources.getInteger(R.integer.post_max_length))
            ),
            submitAction = { submit(it) }
        )
    }
}
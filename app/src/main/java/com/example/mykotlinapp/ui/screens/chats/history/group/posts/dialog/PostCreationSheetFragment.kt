package com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogCreatePostBinding
import com.example.mykotlinapp.model.dto.inputs.form.post.CreatePostInput
import com.example.mykotlinapp.ui.BottomSheetFormFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.MaxLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.ValueRequiredValidator
import kotlinx.coroutines.Job

class PostCreationSheetFragment(
    confirmFunction: (CreatePostInput) -> Job?,
) : BottomSheetFormFragment<CreatePostInput>(confirmFunction) {

    private lateinit var binding: DialogCreatePostBinding

    override val dialogFragmentTag: String = "PostCreationDialogFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreatePostBinding.inflate(inflater)
        binding.postCreationClose.setOnClickListener { dismiss() }
        binding.postCreationSubmit.setOnClickListener { getFormValidator().performSubmitAction() }
        return binding.root
    }

    override fun getFormValidator(): FormValidator<CreatePostInput> {
        val content = binding.createPostContentField
        return FormValidator(
            requireContext(),
            binding.root,
            CreatePostInput(content.getContent()),
            TextInputFieldValidator(
                content,
                ValueRequiredValidator,
                MaxLengthValidator(resources.getInteger(R.integer.post_max_length))
            ),
            submitAction = { submit(it) }
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        binding.createPostContentField.text?.clear()
        super.onDismiss(dialog)
    }
}
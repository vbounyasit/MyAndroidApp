package com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogCreateCommentBinding
import com.example.mykotlinapp.model.dto.inputs.form.comment.CreateCommentInput
import com.example.mykotlinapp.ui.BottomSheetFormFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.MaxLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.ValueRequiredValidator
import kotlinx.coroutines.Job

class CommentCreationSheetFragment(
    confirmFunction: (CreateCommentInput) -> Job?,
) : BottomSheetFormFragment<CreateCommentInput>(confirmFunction) {

    lateinit var binding: DialogCreateCommentBinding

    override val dialogFragmentTag: String = "CommentCreationDialogFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreateCommentBinding.inflate(inflater)
        binding.commentCreationClose.setOnClickListener { dismiss() }
        binding.commentCreationSubmit.setOnClickListener { getFormValidator().performSubmitAction() }
        binding.replyingTo = initialInput?.replyingTo
        return binding.root
    }

    override fun getFormValidator(): FormValidator<CreateCommentInput> {
        val content = binding.createCommentContentField
        return FormValidator(
            requireContext(),
            binding.root,
            initialInput?.let {
                CreateCommentInput(
                    it.parentRemoteId,
                    content.getContent(),
                    it.replyingTo
                )
            },
            TextInputFieldValidator(
                content,
                ValueRequiredValidator,
                MaxLengthValidator(resources.getInteger(R.integer.comment_max_length))
            ),
            submitAction = { submit(it) }
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        binding.createCommentContentField.text?.clear()
        super.onDismiss(dialog)
    }
}
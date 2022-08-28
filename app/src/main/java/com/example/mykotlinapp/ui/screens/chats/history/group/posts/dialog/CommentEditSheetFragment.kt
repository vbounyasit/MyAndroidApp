package com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogEditCommentBinding
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentInput
import com.example.mykotlinapp.ui.BottomSheetFormFragment
import com.example.mykotlinapp.ui.components.form.FormValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.example.mykotlinapp.ui.components.form.validators.MaxLengthValidator
import com.example.mykotlinapp.ui.components.form.validators.ValueRequiredValidator
import kotlinx.coroutines.Job

class CommentEditSheetFragment(
    confirmFunction: (UpdateCommentInput) -> Job?,
) : BottomSheetFormFragment<UpdateCommentInput>(confirmFunction) {

    lateinit var binding: DialogEditCommentBinding

    override val dialogFragmentTag: String = "CommentEditDialogFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditCommentBinding.inflate(inflater)
        binding.commentData = initialInput
        binding.editCommentDialogOptions.dialogCancelButton.setOnClickListener { dismiss() }
        binding.editCommentDialogOptions.dialogSubmitButton.setOnClickListener { getFormValidator().performSubmitAction() }
        return binding.root
    }

    override fun getFormValidator(): FormValidator<UpdateCommentInput> {
        val content = binding.editCommentContentField
        return FormValidator(
            requireContext(),
            binding.root,
            initialInput?.let { UpdateCommentInput(it.remoteId, content.getContent()) },
            TextInputFieldValidator(
                content,
                ValueRequiredValidator,
                MaxLengthValidator(resources.getInteger(R.integer.comment_max_length))
            ),
            submitAction = { submit(it) }
        )
    }
}
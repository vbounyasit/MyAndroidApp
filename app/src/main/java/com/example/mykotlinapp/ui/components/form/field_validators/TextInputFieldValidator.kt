package com.example.mykotlinapp.ui.components.form.field_validators

import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.mykotlinapp.ui.components.form.FieldValidator
import com.example.mykotlinapp.ui.components.form.ValidationErrorData
import com.example.mykotlinapp.ui.components.form.validators.ValidatorData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TextInputFieldValidator(
    private val textInput: TextInputEditText,
    private vararg val data: ValidatorData<TextInputEditText>,
    private val clearOnSubmit: Boolean = true,
) : FieldValidator<TextInputEditText>(textInput) {

    override val validators: List<ValidatorData<TextInputEditText>>
        get() = listOf(*data)

    override val errorData: ValidationErrorData
        get() = run {
            val inputLayout = textInput.parent.parent
            ValidationErrorData(
                {
                    if (inputLayout is TextInputLayout) {
                        inputLayout.error = it
                        inputLayout.isErrorEnabled = true
                    }
                },
                {
                    if (inputLayout is TextInputLayout) {
                        inputLayout.error = null
                        inputLayout.isErrorEnabled = false
                    }
                }
            )
        }

    init {
        field.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) errorData.hideErrorMessage()
        }
    }

    override fun beforeSubmit() {
        super.beforeSubmit()
        textInput.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun onSubmit() {
        super.onSubmit()
        if (clearOnSubmit)
            textInput.text?.clear()
    }

    companion object {
        fun TextInputEditText.getContent(): String {
            return this.text.toString().trim()
        }
    }
}
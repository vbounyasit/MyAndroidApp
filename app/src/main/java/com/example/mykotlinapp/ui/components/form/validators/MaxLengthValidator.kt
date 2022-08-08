package com.example.mykotlinapp.ui.components.form.validators

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.google.android.material.textfield.TextInputEditText

class MaxLengthValidator(private val maxLength: Int) : ValidatorData<TextInputEditText> {
    override fun getErrorMessage(context: Context): String {
        return String.format(context.getString(R.string.error_max_char_allowed), maxLength)
    }

    override fun isValid(field: TextInputEditText): Boolean {
        return field.getContent().length <= maxLength
    }
}
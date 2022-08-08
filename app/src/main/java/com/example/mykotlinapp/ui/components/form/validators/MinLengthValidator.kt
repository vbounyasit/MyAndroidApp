package com.example.mykotlinapp.ui.components.form.validators

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.google.android.material.textfield.TextInputEditText

open class MinLengthValidator(private val minLength: Int) : ValidatorData<TextInputEditText> {
    override fun getErrorMessage(context: Context): String {
        return String.format(context.getString(R.string.error_min_char_required), minLength)
    }

    override fun isValid(field: TextInputEditText): Boolean {
        return field.getContent().length >= minLength || field.getContent().isBlank()
    }
}
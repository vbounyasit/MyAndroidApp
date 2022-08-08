package com.example.mykotlinapp.ui.components.form.validators

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.google.android.material.textfield.TextInputEditText

object ValueRequiredValidator : ValidatorData<TextInputEditText> {
    override fun getErrorMessage(context: Context): String {
        return context.getString(R.string.error_information_required)
    }

    override fun isValid(field: TextInputEditText): Boolean {
        return field.getContent().isNotBlank()
    }
}
package com.example.mykotlinapp.ui.components.form.validators

import android.content.Context
import android.util.Patterns
import com.example.mykotlinapp.R
import com.example.mykotlinapp.ui.components.form.field_validators.TextInputFieldValidator.Companion.getContent
import com.google.android.material.textfield.TextInputEditText

object EmailValidator : ValidatorData<TextInputEditText> {
    override fun getErrorMessage(context: Context): String {
        return context.getString(R.string.error_invalid_email_address)
    }

    override fun isValid(field: TextInputEditText): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(field.getContent()).matches()
    }
}
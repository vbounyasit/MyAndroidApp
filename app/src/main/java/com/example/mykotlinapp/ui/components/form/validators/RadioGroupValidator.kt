package com.example.mykotlinapp.ui.components.form.validators

import android.content.Context
import android.widget.RadioGroup
import com.example.mykotlinapp.R

class RadioGroupValidator(private val label: String) : ValidatorData<RadioGroup> {
    override fun getErrorMessage(context: Context): String {
        return String.format(context.getString(R.string.error_radio_group), label)
    }

    override fun isValid(field: RadioGroup): Boolean {
        return field.checkedRadioButtonId != -1
    }
}
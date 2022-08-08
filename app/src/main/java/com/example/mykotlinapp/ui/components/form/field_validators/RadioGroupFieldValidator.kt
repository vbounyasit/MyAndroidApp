package com.example.mykotlinapp.ui.components.form.field_validators

import android.content.Context
import android.widget.RadioGroup
import android.widget.Toast
import com.example.mykotlinapp.ui.components.form.FieldValidator
import com.example.mykotlinapp.ui.components.form.ValidationErrorData
import com.example.mykotlinapp.ui.components.form.validators.RadioGroupValidator
import com.example.mykotlinapp.ui.components.form.validators.ValidatorData

class RadioGroupFieldValidator(val context: Context, radioGroup: RadioGroup) : FieldValidator<RadioGroup>(radioGroup) {
    override val validators: List<ValidatorData<RadioGroup>>
        get() = listOf(RadioGroupValidator("a Gender"))
    override val errorData: ValidationErrorData
        get() = ValidationErrorData({ Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }, {})
}
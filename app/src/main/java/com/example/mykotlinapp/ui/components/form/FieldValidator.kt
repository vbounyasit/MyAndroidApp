package com.example.mykotlinapp.ui.components.form

import android.content.Context
import com.example.mykotlinapp.ui.components.form.validators.ValidatorData

/**
 * Helper class for a Form's field validation
 *
 * @param T The type of the field to validate (TextInputField/etc...)
 * @property field The field to validate
 */
abstract class FieldValidator<T>(val field: T) {
    abstract val validators: List<ValidatorData<T>>
    abstract val errorData: ValidationErrorData

    fun validate(context: Context): Boolean {
        for (validatorData in validators) {
            if (!validatorData.isValid(field)) {
                errorData.displayErrorMessage(validatorData.getErrorMessage(context))
                return false
            } else errorData.hideErrorMessage()
        }
        return true
    }

    open fun beforeSubmit() {}

    open fun onSubmit() {}

}
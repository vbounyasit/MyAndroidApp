package com.example.mykotlinapp.ui.components.form

import android.content.Context
import android.view.View
import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

/**
 * Helper class for Form validator
 *
 * @param T The type of the form's input object
 * @property context The context of the fragment/activity containing the form
 * @property formRootView The root view containing the form (LinearLayout/RelativeLayout/etc...)
 * @property input The form's input object to submit
 * @property fieldValidators The validators for the form's fields to apply before allowing submission
 * @property submitAction The form submission function
 */
class FormValidator<T : InputDTO>(
    val context: Context,
    private val formRootView: View,
    private val input: T?,
    private vararg val fieldValidators: FieldValidator<*>,
    val submitAction: (T) -> Unit,
) {

    /**
     * The validation function
     *
     * @return Whether the form is valid or not
     */
    fun validate(): Boolean {
        for (validator in fieldValidators)
            if (!validator.validate(context))
                return false
        return true
    }

    /**
     * Perform submit action
     *
     */
    fun performSubmitAction() {
        if (validate()) {
            formRootView.clearFocus()
            for (validator in fieldValidators)
                validator.beforeSubmit()
            input?.let { submitAction(it) }
            for (validator in fieldValidators)
                validator.onSubmit()
        }
    }
}
package com.example.mykotlinapp.ui.components.form

/**
 * Validation error configuration
 *
 * @property displayErrorMessage The action to execute to display the error on the UI
 * @property hideErrorMessage The action to execute to hide the error from the UI
 */
data class ValidationErrorData(
    val displayErrorMessage: (String) -> Unit,
    val hideErrorMessage: () -> Unit,
)
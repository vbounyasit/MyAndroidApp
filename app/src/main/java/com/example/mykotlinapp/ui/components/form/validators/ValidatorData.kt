package com.example.mykotlinapp.ui.components.form.validators

import android.content.Context

sealed interface ValidatorData<T> {
    fun getErrorMessage(context: Context): String
    fun isValid(field: T): Boolean
}
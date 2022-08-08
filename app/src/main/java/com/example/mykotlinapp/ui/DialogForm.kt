package com.example.mykotlinapp.ui

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO
import com.example.mykotlinapp.ui.components.form.FormValidator

interface DialogForm<T : InputDTO> {
    var initialInput: T?

    fun getFormValidator(): FormValidator<T>

    val dialogFragmentTag: String

    fun submit(input: T): Unit
}
package com.example.mykotlinapp.ui

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job

abstract class BottomSheetFormFragment<T : InputDTO>(
    private val confirmFunction: (T) -> Job?,
) : BottomSheetDialogFragment(), DialogForm<T> {

    override var initialInput: T? = null


    override fun submit(input: T) {
        confirmFunction(input)?.invokeOnCompletion { dismiss() }
    }
}
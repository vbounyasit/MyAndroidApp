package com.example.mykotlinapp.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.mykotlinapp.R
import com.example.mykotlinapp.model.dto.inputs.form.InputDTO
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job

abstract class DialogFormFragment<T : InputDTO>(
    private val confirmFunction: (T) -> Job?,
) : DialogFragment(), DialogForm<T> {

    open val isFullScreen: Boolean = true
    open val cancelAlert: CancelAlert? = null
    override var initialInput: T? = null

    open lateinit var cancelButton: View
    open lateinit var submitButton: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelButton.setOnClickListener {
            cancelAlert?.let {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(it.title)
                    .setMessage(it.message)
                    .setNeutralButton(resources.getString(R.string.cancel_label), null)
                    .setPositiveButton(it.confirmLabel) { _, _ -> dismiss() }
                    .show()
            } ?: dismiss()
        }
        submitButton.setOnClickListener { getFormValidator().performSubmitAction() }
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        if (isFullScreen)
            params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    override fun submit(input: T) {
        confirmFunction(input)?.invokeOnCompletion { dismiss() }
    }

    data class CancelAlert(val title: String, val message: String, val confirmLabel: String)
}
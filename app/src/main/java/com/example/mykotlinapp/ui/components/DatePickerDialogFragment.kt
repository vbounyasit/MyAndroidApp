package com.example.mykotlinapp.ui.components

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.mykotlinapp.R
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Date picker dialog fragment for data form fields
 *
 * @property displayText The text field to display the date on
 */
class DatePickerDialogFragment(private val displayText: TextInputEditText) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), R.style.DatePicker, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        //NOTE: months are indexed from 0
        val date = LocalDate.of(year, month + 1, day)
        displayText.setText(date.format(formatter))
    }
}
package com.example.mykotlinapp.ui.screens.chats.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.DialogContactInfoBinding
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ContactInfoDialogFragment(
    private val userContactDTO: UserContactDTO,
    private val removeOperation: () -> Unit = {},
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogContactInfoBinding.inflate(inflater)
        binding.property = userContactDTO
        binding.contactInfoRemove.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.remove_contact_alert))
                .setMessage(getString(R.string.remove_contact_alert_message))
                .setNeutralButton(resources.getString(R.string.cancel_label), null)
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    removeOperation()
                    this.dismiss()
                }
                .show()
        }
        return binding.root
    }

    companion object {
        const val TAG = "contactInfo"
    }

}
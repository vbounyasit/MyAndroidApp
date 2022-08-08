package com.example.mykotlinapp.ui.components

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.mykotlinapp.ui.DialogForm

/**
 * Helper class representing the Dialog manager component
 * Allows a fragment to open various dialog fragments containing an input form to submit
 * @Injected in an activity viewModel
 */
class DialogFormFragmentManager {

    private var _dialogFragments = mutableMapOf<String, DialogForm<*>>()
    private var _pendingOpenDialogTag = MutableLiveData<String?>()

    /**
     * Usually called on the parent activity for its fragments
     * Registers the observers for form dialogs
     *
     * @param lifecycleOwner The lifecycle owner to register the observers to
     * @param fragmentManager The fragment manager object
     */
    fun registerLifeCycle(lifecycleOwner: LifecycleOwner, fragmentManager: FragmentManager) {
        _pendingOpenDialogTag.observe(lifecycleOwner) {
            it?.let {
                _dialogFragments[it]?.let { dialog ->
                    if (dialog is DialogFragment) {
                        dialog.show(fragmentManager, it)
                        _pendingOpenDialogTag.value = null
                    }
                }
            }
        }
    }

    /**
     * Usually called on a fragment that runs in an Activity to register a dialog screen to it
     * @Note: it is required to register each dialog before being able to open/close them
     *
     * @param dialogFragment The dialog fragment to register
     */
    fun registerDialogForm(dialogFragment: DialogForm<*>) {
        _dialogFragments[dialogFragment.dialogFragmentTag] = dialogFragment
    }

    /**
     * Opens a form dialog
     *
     * @param tag The tag of the dialog form to open
     */
    fun openDialogForm(tag: String) {
        _pendingOpenDialogTag.value = tag
    }

}
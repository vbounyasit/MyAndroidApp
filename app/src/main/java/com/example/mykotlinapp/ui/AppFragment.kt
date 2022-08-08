package com.example.mykotlinapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

open class AppFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerObservers()
        registerListeners()
        registerComponents()
        super.onViewCreated(view, savedInstanceState)
    }

    open fun registerObservers() {}

    open fun registerListeners() {}

    open fun registerComponents() {}

    companion object {
        fun attach(fragment: Fragment, bundle: Bundle): Fragment {
            fragment.arguments = bundle
            return fragment
        }
    }
}
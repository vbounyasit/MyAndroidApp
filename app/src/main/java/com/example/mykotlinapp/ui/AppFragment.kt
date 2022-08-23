package com.example.mykotlinapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * base class implemented by all fragments
 */
open class AppFragment : Fragment(), WithComponents {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerComponents()
    }

    companion object {
        fun attach(fragment: Fragment, bundle: Bundle): Fragment {
            fragment.arguments = bundle
            return fragment
        }
    }
}
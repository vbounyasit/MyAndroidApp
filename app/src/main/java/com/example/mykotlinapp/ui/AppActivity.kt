package com.example.mykotlinapp.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

/**
 * base class implemented by all activities
 */
open class AppActivity: AppCompatActivity(), WithComponents {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        registerComponents()
    }
}
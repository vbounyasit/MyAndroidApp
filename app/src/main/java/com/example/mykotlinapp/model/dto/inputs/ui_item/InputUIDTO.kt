package com.example.mykotlinapp.model.dto.inputs.ui_item

import android.os.Parcelable
import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

/**
 * An interface for recycler view items input used in Adapter payloads to dynamically update them
 */
interface InputUIDTO : Parcelable, InputDTO {
    /**
     * The remote id to identify the recycler view item on the list
     */
    val remoteId: String
}
package com.example.mykotlinapp.model.dto.inputs.ui_item.impl

import com.example.mykotlinapp.model.dto.inputs.ui_item.InputUIDTO
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdatePostNotificationInputUI(
    override val remoteId: String,
    val notificationEnabled: Boolean,
) : InputUIDTO
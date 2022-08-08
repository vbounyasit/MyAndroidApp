package com.example.mykotlinapp.model.dto.inputs.ui_item.impl

import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.model.dto.inputs.ui_item.InputUIDTO
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateContactInputUI(
    override val remoteId: String,
    val relationType: ContactRelationType,
) : InputUIDTO
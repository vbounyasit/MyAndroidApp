package com.example.mykotlinapp.model.dto.ui.user

import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

data class UserContactSelectionDTO(
    val dto: UserContactDTO,
    val selected: Boolean,
) : RecyclerViewItem {
    override val remoteId: String = dto.remoteId
}
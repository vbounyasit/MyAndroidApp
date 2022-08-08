package com.example.mykotlinapp.model.dto.ui

import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

interface Participant : RecyclerViewItem {
    val profilePicture: String
}
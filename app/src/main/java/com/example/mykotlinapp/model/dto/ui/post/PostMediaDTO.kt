package com.example.mykotlinapp.model.dto.ui.post

import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

data class PostMediaDTO(
    override val remoteId: String,
    val media: String,
) : RecyclerViewItem
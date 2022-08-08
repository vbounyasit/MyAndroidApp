package com.example.mykotlinapp.ui.components.recycler_view

/**
 * Interface representing a recycler view item
 */
interface RecyclerViewItem {
    val remoteId: String

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
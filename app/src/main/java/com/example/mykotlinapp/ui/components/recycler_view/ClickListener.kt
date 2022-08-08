package com.example.mykotlinapp.ui.components.recycler_view

/**
 * Click listener class to pass to items in the ListAdapter
 *
 * @param Property The type of data bound to each item
 * @property listener The listener function to apply
 */
class ClickListener<Property>(val listener: (Property) -> Unit) {
    fun onClick(property: Property) = listener(property)
}
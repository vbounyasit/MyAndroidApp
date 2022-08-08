package com.example.mykotlinapp.ui.components.recycler_view

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * App class for defining a ViewHolder
 *
 */
class AppViewHolder<Binding : ViewDataBinding>(var binding: Binding) : ViewHolder(binding.root) {
    fun bind(applyBinding: (Binding) -> Unit) {
        applyBinding(binding)
        binding.executePendingBindings()
    }
}
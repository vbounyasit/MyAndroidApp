package com.example.mykotlinapp.ui.screens.chats.history.group.posts.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.mykotlinapp.databinding.ComponentUserPostMediasBinding
import com.example.mykotlinapp.model.dto.ui.post.PostMediaDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppViewHolder

class PostMediasAdapter(val context: Context, private val medias: List<PostMediaDTO>) :
    Adapter<AppViewHolder<ComponentUserPostMediasBinding>>() {

    override fun getItemCount(): Int {
        return medias.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppViewHolder<ComponentUserPostMediasBinding> {
        return AppViewHolder(
            ComponentUserPostMediasBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: AppViewHolder<ComponentUserPostMediasBinding>,
        position: Int
    ) {
        holder.bind {
            it.media = medias[position].media
        }
    }
}
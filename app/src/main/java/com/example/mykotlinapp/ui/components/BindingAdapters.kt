package com.example.mykotlinapp.ui.components

import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mykotlinapp.R
import com.example.mykotlinapp.model.dto.ui.post.PostMediaDTO
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.comments.PostMediasAdapter

@BindingAdapter("listData")
@Suppress("UNCHECKED_CAST")
fun <T> bindRecyclerViewData(
    recyclerView: RecyclerView,
    data: List<T>?,
) {
    val adapter = recyclerView.adapter as? ListAdapter<T, *>
    adapter?.submitList(data)
}

@BindingAdapter("circleImgUrl")
fun bindCircleImage(imgView: ImageView, circleImgUrl: String?) {
    circleImgUrl?.let { url ->
        val imgUri = url.toUri().buildUpon().scheme("https").build()
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_download_failed)
            .circleCrop()
        Glide
            .with(imgView.context)
            .load(imgUri)
            .apply(requestOptions)
            .into(imgView)
    }
}


@BindingAdapter("imgUrl")
fun bindImage(imgView: ImageView, circleImgUrl: String?) {
    circleImgUrl?.let { url ->
        val imgUri = url.toUri().buildUpon().scheme("https").build()
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_download_failed)
        Glide
            .with(imgView.context)
            .load(imgUri)
            .apply(requestOptions)
            .into(imgView)
    }
}

@BindingAdapter("medias")
fun bindMediasAdapter(viewPager: ViewPager2, medias: List<PostMediaDTO>?) {
    medias?.let {
        viewPager.adapter = PostMediasAdapter(viewPager.context, it)
    }
}

@BindingAdapter("layout_width", "layout_height")
fun bindDimensions(imgView: ImageView, layoutHeight: Int?, layoutWidth: Int?) {
    layoutHeight?.let { height ->
        layoutWidth?.let { width ->
            val params = RelativeLayout.LayoutParams(width, height)
            imgView.layoutParams = params
        }
    }
}


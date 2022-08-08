package com.example.mykotlinapp.model.dto.ui.post

import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

data class PostDTO(
    override val remoteId: String,
    val groupRemoteId: String,
    val posterName: String,
    val posterProfilePicture: String,
    val content: String,
    val timePosted: String,
    val medias: List<PostMediaDTO>,
    val votesCount: String,
    val commentsCount: String,
    val voteState: VoteState,
    val isCreator: Boolean,
    val isEdited: Boolean,
) : RecyclerViewItem
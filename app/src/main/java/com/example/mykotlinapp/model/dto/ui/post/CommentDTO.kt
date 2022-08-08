package com.example.mykotlinapp.model.dto.ui.post

import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

data class CommentDTO(
    override val remoteId: String,
    val commenterName: String,
    val commenterProfilePicture: String,
    val content: String,
    val time: String,
    val votesCount: String,
    val depthLevel: Int,
    val isLastReply: Boolean,
    val depthFlags: String,
    val voteState: VoteState,
    val isCreator: Boolean,
    val isEdited: Boolean,
) : RecyclerViewItem
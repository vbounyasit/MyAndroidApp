package com.example.mykotlinapp.model.dto.inputs.ui_item.impl

import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.model.dto.inputs.ui_item.InputUIDTO
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdatePostVoteInputUI(
    override val remoteId: String,
    val voteState: VoteState,
    val votesCount: String,
) : InputUIDTO
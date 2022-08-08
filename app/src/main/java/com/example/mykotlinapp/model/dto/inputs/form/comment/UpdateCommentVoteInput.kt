package com.example.mykotlinapp.model.dto.inputs.form.comment

import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

data class UpdateCommentVoteInput(
    val remoteId: String,
    val voteState: VoteState,
    val votesCountDelta: Int,
) : InputDTO
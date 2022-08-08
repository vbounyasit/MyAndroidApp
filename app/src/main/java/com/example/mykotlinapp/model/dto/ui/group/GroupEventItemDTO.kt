package com.example.mykotlinapp.model.dto.ui.group

import com.example.mykotlinapp.domain.pojo.EventJoinState
import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

data class GroupEventItemDTO(
    override val remoteId: String,
    val eventPicture: String,
    val eventName: String,
    val eventSummary: String,
    val eventTime: String,
    val joinState: EventJoinState,
    val participants: List<GroupEventParticipantDTO>,
    val extraParticipantCount: Int,
) : RecyclerViewItem
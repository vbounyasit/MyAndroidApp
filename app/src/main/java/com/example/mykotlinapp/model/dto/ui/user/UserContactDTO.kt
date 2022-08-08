package com.example.mykotlinapp.model.dto.ui.user

import com.example.mykotlinapp.domain.pojo.ActivityStatus
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

data class UserContactDTO(
    override val remoteId: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val profilePicture: String,
    val description: String?,
    val activityStatus: ActivityStatus?,
    val relationType: ContactRelationType,
) : RecyclerViewItem
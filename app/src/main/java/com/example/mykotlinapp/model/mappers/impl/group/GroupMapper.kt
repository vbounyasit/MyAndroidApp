package com.example.mykotlinapp.model.mappers.impl.group

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.group.GroupDTO
import com.example.mykotlinapp.model.dto.ui.post.PostGroupData
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.network.dto.responses.chat.ChatResponse

object GroupMapper :
    DTOMapperWithParam<GroupProperty, GroupDTO, Context>,
    NetworkResponseMapper<ChatResponse, GroupProperty> {

    fun toPostGroupData(context: Context): (GroupProperty) -> PostGroupData =
        { entity ->
            PostGroupData(
                entity.groupName,
                entity.groupPicture.split(context.getString(R.string.profile_pictures_delimiter)),
                entity.isAdmin
            )
        }

    override fun toEntity(networkData: ChatResponse): GroupProperty =
        GroupProperty(
            networkData.group.remoteId,
            networkData.chat.remoteId,
            networkData.chat.name,
            networkData.chat.profilePicture,
            networkData.group.description,
            networkData.group.backgroundPicture,
            networkData.participantsData.remainingCount,
            networkData.lastGroupReadTime,
            networkData.isAdmin,
            SyncState.UP_TO_DATE,
            networkData.creationTimeStamp,
            networkData.updateTimeStamp
        )

    override fun toDTO(parameter: Context): (entity: GroupProperty) -> GroupDTO =
        { entity ->
            val groupPictures: List<String> =
                entity.groupPicture.split(parameter.getString(R.string.profile_pictures_delimiter))
            val groupName = with(entity.groupName) {
                if (contains(parameter.getString(R.string.profile_pictures_delimiter)))
                    parameter.getString(R.string.default_group_name)
                else
                    entity.groupName
            }
            GroupDTO(
                entity.remoteId,
                entity.chatRemoteId,
                groupName,
                entity.groupDescription,
                groupPictures,
                entity.groupBackgroundPicture,
                entity.participantExtraCount,
                entity.isAdmin
            )
        }
}
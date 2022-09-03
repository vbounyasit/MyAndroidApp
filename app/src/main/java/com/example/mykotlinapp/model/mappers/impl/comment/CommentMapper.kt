package com.example.mykotlinapp.model.mappers.impl.comment

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.post.CommentDTO
import com.example.mykotlinapp.model.entity.post.UserComment
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.getVoteStateValue
import com.example.mykotlinapp.model.mappers.impl.Utils.toFormattedTimeAgo
import com.example.mykotlinapp.network.dto.responses.post.CommentResponse

object CommentMapper :
    DTOMapperWithParam<List<UserComment>, List<CommentDTO>, Context>,
    NetworkResponseMapper<List<CommentResponse>, List<UserComment>> {

    override fun toEntity(networkData: List<CommentResponse>): List<UserComment> {
        return networkData.mapIndexed { index, commentResponse ->
            UserComment(
                commentResponse.remoteId,
                commentResponse.postRemoteId,
                commentResponse.groupRemoteId,
                commentResponse.parentRemoteId,
                commentResponse.creator.fullName,
                commentResponse.creator.profilePicture,
                commentResponse.content,
                commentResponse.voteCount,
                commentResponse.depthLevel,
                commentResponse.isLast,
                commentResponse.voteState.getVoteStateValue(),
                commentResponse.isCreator,
                index,
                SyncState.UP_TO_DATE,
                commentResponse.creationTimeStamp,
                commentResponse.updateTimeStamp
            )
        }
    }

    override fun toDTO(parameter: Context): (entity: List<UserComment>) -> List<CommentDTO> =
        { entity ->
            val depthFLags = BooleanArray(parameter.resources.getInteger(R.integer.comments_max_depth)) { true }
            entity.map {
                val timePosted: String = toFormattedTimeAgo(parameter, it.creationTime)
                if (it.isLast && it.depthLevel > 0) depthFLags[it.depthLevel - 1] = false
                CommentDTO(
                    it.remoteId,
                    it.commenterName,
                    it.commenterProfilePicture,
                    it.content,
                    timePosted,
                    it.votesCount.toString(),
                    it.depthLevel,
                    it.isLast,
                    depthFLags.joinToString(
                        separator = parameter.getString(R.string.profile_pictures_delimiter),
                        transform = Boolean::toString
                    ),
                    it.voteState,
                    it.isCreator,
                    it.updateTime > it.creationTime
                )
            }
        }

}
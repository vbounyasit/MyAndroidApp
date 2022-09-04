package com.example.mykotlinapp.model.mappers.impl.post

import android.content.Context
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.model.entity.post.PostMedia
import com.example.mykotlinapp.model.entity.post.UserPost
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.getFormattedAmount
import com.example.mykotlinapp.model.mappers.impl.Utils.getVoteStateValue
import com.example.mykotlinapp.model.mappers.impl.Utils.toFormattedTimeAgo
import com.example.mykotlinapp.network.dto.responses.post.PostResponse
import kotlin.collections.Map.Entry

object PostMapper : DTOMapperWithParam<Pair<UserPost, List<PostMedia>>, PostDTO, Context>,
    NetworkResponseMapper<PostResponse, UserPost> {

    override fun toEntity(networkData: PostResponse): UserPost {
        return UserPost(
            networkData.remoteId,
            networkData.groupRemoteId,
            networkData.creator.fullName,
            networkData.creator.profilePicture,
            networkData.content,
            networkData.votesCount,
            networkData.commentsCount,
            networkData.voteState.getVoteStateValue(),
            networkData.isCreator,
            SyncState.UP_TO_DATE,
            networkData.creationTimeStamp,
            networkData.updateTimeStamp
        )
    }

    override fun toDTO(parameter: Context): (entity: Pair<UserPost, List<PostMedia>>) -> PostDTO =
        { (post, media) ->
            val timePosted: String = toFormattedTimeAgo(parameter, post.creationTime)
            PostDTO(
                post.remoteId,
                post.groupRemoteId,
                post.posterName,
                post.posterProfilePicture,
                post.content,
                timePosted,
                media.map(PostMediasMapper::toDTO),
                post.votesCount.getFormattedAmount(),
                post.commentsCount.getFormattedAmount(),
                post.voteState,
                post.isCreator,
                post.updateTime > post.creationTime
            )
        }

}
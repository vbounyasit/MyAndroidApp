package com.example.mykotlinapp.model.mappers.impl.post

import android.content.Context
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.model.entity.post.PostMedia
import com.example.mykotlinapp.model.entity.post.UserPost
import com.example.mykotlinapp.model.mappers.DTOContextMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils.getFormattedAmount
import com.example.mykotlinapp.model.mappers.impl.Utils.getVoteStateValue
import com.example.mykotlinapp.model.mappers.impl.Utils.toTimeAgo
import com.example.mykotlinapp.network.dto.responses.post.PostResponse

object PostMapper : DTOContextMapper<Pair<UserPost, List<PostMedia>>, PostDTO>,
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

    override fun toDTO(context: Context): (entity: Pair<UserPost, List<PostMedia>>) -> PostDTO =
        { (post, media) ->
            val timePosted: String = toTimeAgo(context, post.creationTime)
            PostDTO(
                post.remoteId,
                post.groupRemoteId,
                post.posterName,
                post.posterProfilePicture,
                post.content,
                timePosted,
                media.map { PostMediasMapper.toDTO(it) },
                post.votesCount.getFormattedAmount(),
                post.commentsCount.getFormattedAmount(),
                post.voteState,
                post.isCreator,
                post.updateTime > post.creationTime
            )
        }

}
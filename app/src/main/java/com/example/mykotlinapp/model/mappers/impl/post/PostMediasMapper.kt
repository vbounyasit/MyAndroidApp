package com.example.mykotlinapp.model.mappers.impl.post

import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.post.PostMediaDTO
import com.example.mykotlinapp.model.entity.post.PostMedia
import com.example.mykotlinapp.model.mappers.DTOMapper
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.network.dto.responses.post.PostResponse

object PostMediasMapper : DTOMapper<PostMedia, PostMediaDTO>,
    NetworkResponseMapper<PostResponse, List<PostMedia>> {
    override fun toDTO(entity: PostMedia): PostMediaDTO =
        PostMediaDTO(
            entity.postRemoteId,
            entity.media
        )

    override fun toEntity(networkData: PostResponse): List<PostMedia> =
        networkData.medias.map {
            PostMedia(it.remoteId, networkData.remoteId, it.content, SyncState.UP_TO_DATE)
        }
}
package com.example.mykotlinapp.model.entity.post

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState

@Entity(tableName = "post_medias")
class PostMedia(
    @PrimaryKey
    @ColumnInfo(name = "post_media_remote_id")
    val remoteId: String,
    @ColumnInfo(name = "parent_post_remote_id")
    val postRemoteId: String,
    @ColumnInfo(name = "media")
    val media: String,
    @ColumnInfo(name = "sync_state")
    val syncState: SyncState,
)
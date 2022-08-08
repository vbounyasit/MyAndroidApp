package com.example.mykotlinapp.model.entity.post

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.domain.pojo.VoteState

@Entity(tableName = "user_posts")
data class UserPost(
    @PrimaryKey
    @ColumnInfo(name = "post_remote_id")
    val remoteId: String,
    @ColumnInfo(name = "parent_group_remote_id")
    val groupRemoteId: String,
    @ColumnInfo(name = "poster_name")
    val posterName: String,
    @ColumnInfo(name = "poster_profile_picture")
    val posterProfilePicture: String,
    @ColumnInfo(name = "post_time")
    val time: Long,
    @ColumnInfo(name = "edit_time")
    val editTime: Long?,
    @ColumnInfo(name = "post_content")
    val content: String,
    @ColumnInfo(name = "votes_count")
    val votesCount: Int,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,
    @ColumnInfo(name = "vote_state")
    val voteState: VoteState,
    @ColumnInfo(name = "is_creator")
    val isCreator: Boolean,
    @ColumnInfo(name = "sync_state")
    val syncState: SyncState,
)
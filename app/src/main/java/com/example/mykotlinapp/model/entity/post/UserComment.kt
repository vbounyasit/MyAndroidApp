package com.example.mykotlinapp.model.entity.post

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.model.entity.TimeStampData

@Entity(tableName = "user_comments")
data class UserComment(
    @PrimaryKey
    @ColumnInfo(name = "remote_id")
    override val remoteId: String,
    @ColumnInfo(name = "parent_post_remote_id")
    val parentPostRemoteId: String,
    @ColumnInfo(name = "parent_group_remote_id")
    val parentGroupRemoteId: String,
    @ColumnInfo(name = "parent_comment_remote_id")
    val parentCommentRemoteId: String?,
    @ColumnInfo(name = "commenter_name")
    val commenterName: String,
    @ColumnInfo(name = "commenter_picture")
    val commenterProfilePicture: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "votes_count")
    val votesCount: Int,
    @ColumnInfo(name = "depth_level")
    val depthLevel: Int,
    @ColumnInfo(name = "is_last")
    val isLast: Boolean,
    @ColumnInfo(name = "vote_state")
    val voteState: VoteState,
    @ColumnInfo(name = "is_creator")
    val isCreator: Boolean,
    @ColumnInfo(name = "order_index")
    val index: Int,
    @ColumnInfo(name = "sync_state")
    override val syncState: SyncState,
    @ColumnInfo(name = "creation_time")
    override val creationTime: Long,
    @ColumnInfo(name = "update_time")
    override val updateTime: Long
): SyncData, TimeStampData
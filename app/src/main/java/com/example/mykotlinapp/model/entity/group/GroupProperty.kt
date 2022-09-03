package com.example.mykotlinapp.model.entity.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.model.entity.TimeStampData

@Entity(tableName = "groups")
data class GroupProperty(
    @PrimaryKey
    @ColumnInfo(name = "group_remote_id")
    override val remoteId: String,
    @ColumnInfo(name = "linked_chat_remote_id")
    val chatRemoteId: String,
    @ColumnInfo(name = "group_name")
    val groupName: String,
    @ColumnInfo(name = "group_picture")
    val groupPicture: String,
    @ColumnInfo(name = "group_description")
    val groupDescription: String?,
    @ColumnInfo(name = "group_background_picture")
    val groupBackgroundPicture: String?,
    @ColumnInfo(name = "participant_extra_count")
    val participantExtraCount: Int,
    @ColumnInfo(name = "last_read")
    val lastReadTime: Long?,
    @ColumnInfo(name = "is_admin")
    val isAdmin: Boolean,
    @ColumnInfo(name = "group_sync_state")
    override val syncState: SyncState,
    @ColumnInfo(name = "group_creation_time")
    override val creationTime: Long,
    @ColumnInfo(name = "group_update_time")
    override val updateTime: Long
) : SyncData, TimeStampData
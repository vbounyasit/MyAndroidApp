package com.example.mykotlinapp.model.entity.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.model.entity.TimeStampData

@Entity(tableName = "chats")
data class ChatProperty(
    @PrimaryKey
    @ColumnInfo(name = "chat_remote_id")
    override val remoteId: String,
    @ColumnInfo(name = "linked_group_remote_id")
    val groupRemoteId: String,
    @ColumnInfo(name = "chat_name")
    val name: String,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String,
    @ColumnInfo(name = "last_active")
    val lastActive: Long,
    @ColumnInfo(name = "last_read")
    val lastReadTime: Long?,
    @ColumnInfo(name = "is_admin")
    val isAdmin: Boolean,
    @ColumnInfo(name = "sync_state")
    override val syncState: SyncState,
    @ColumnInfo(name = "creation_time")
    override val creationTime: Long,
    @ColumnInfo(name = "update_time")
    override val updateTime: Long
): SyncData, TimeStampData
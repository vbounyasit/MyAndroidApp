package com.example.mykotlinapp.model.entity.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState

@Entity(tableName = "chat_items")
data class ChatItem(
    @PrimaryKey
    @ColumnInfo(name = "chat_remote_id")
    val remoteId: String,
    @ColumnInfo(name = "chat_name")
    val name: String,
    @ColumnInfo(name = "chat_profile_picture")
    val profilePicture: String,
    @ColumnInfo(name = "chat_last_active")
    val lastActive: Long,
    @ColumnInfo(name = "chat_last_read")
    val lastReadTime: Long?,
    @ColumnInfo(name = "chat_is_group")
    val isGroupChat: Boolean,
    @ColumnInfo(name = "chat_sync_state")
    val syncState: SyncState,
)

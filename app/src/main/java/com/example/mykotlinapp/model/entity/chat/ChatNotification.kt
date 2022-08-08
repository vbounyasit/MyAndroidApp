package com.example.mykotlinapp.model.entity.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState


@Entity(tableName = "chat_notifications")
data class ChatNotification(
    @PrimaryKey
    val remoteId: String,
    @ColumnInfo(name = "chat_remote_id")
    val chatRemoteId: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "creation_date")
    val creationDate: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: SyncState,
)
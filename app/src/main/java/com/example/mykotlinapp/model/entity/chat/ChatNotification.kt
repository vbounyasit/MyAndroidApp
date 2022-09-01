package com.example.mykotlinapp.model.entity.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.model.entity.TimeStampData


@Entity(tableName = "chat_notifications")
data class ChatNotification(
    @PrimaryKey
    override val remoteId: String,
    @ColumnInfo(name = "chat_remote_id")
    val chatRemoteId: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "sync_state")
    override val syncState: SyncState,
    @ColumnInfo(name = "creation_time")
    override val creationTime: Long,
    @ColumnInfo(name = "update_time")
    override val updateTime: Long
): SyncData, TimeStampData
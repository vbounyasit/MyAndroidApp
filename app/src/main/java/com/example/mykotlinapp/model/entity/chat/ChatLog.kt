package com.example.mykotlinapp.model.entity.chat

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.model.entity.TimeStampData
import com.example.mykotlinapp.model.entity.user.UserContact

@Entity(tableName = "chat_logs")
data class ChatLog(
    @PrimaryKey
    @ColumnInfo(name = "log_remote_id")
    override val remoteId: String,
    @ColumnInfo(name = "log_chat_remote_id")
    val chatRemoteId: String,
    @Embedded
    val author: UserContact,
    @ColumnInfo(name = "log_content")
    val content: String,
    @ColumnInfo(name = "log_is_me")
    val isMe: Boolean,
    @ColumnInfo(name = "log_sync_state")
    override val syncState: SyncState,
    @ColumnInfo(name = "log_creation_time")
    override val creationTime: Long,
    @ColumnInfo(name = "log_update_time")
    override val updateTime: Long
) : SyncData, TimeStampData

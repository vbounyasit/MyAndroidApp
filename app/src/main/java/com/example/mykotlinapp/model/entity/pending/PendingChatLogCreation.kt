package com.example.mykotlinapp.model.entity.pending

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_chat_logs_creation")
data class PendingChatLogCreation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "chat_remote_id")
    val chatRemoteId: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "creation_date")
    val creationDate: Long,
)
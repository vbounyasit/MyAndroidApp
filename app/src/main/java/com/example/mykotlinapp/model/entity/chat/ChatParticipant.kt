package com.example.mykotlinapp.model.entity.chat

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "chat_participants",
    primaryKeys = ["participant_remote_id", "participant_chat_remote_id"]
)
data class ChatParticipant(
    @ColumnInfo(name = "participant_remote_id")
    val remoteId: String,
    @ColumnInfo(name = "participant_chat_remote_id")
    val chatRemoteId: String,
    @ColumnInfo(name = "participant_first_name")
    val firstName: String,
    @ColumnInfo(name = "participant_last_name")
    val lastName: String,
    @ColumnInfo(name = "participant_full_name")
    val fullName: String,
    @ColumnInfo(name = "participant_profile_picture")
    val profilePicture: String,
    @ColumnInfo(name = "is_admin")
    val isAdmin: Boolean,
    @ColumnInfo(name = "last_read_time")
    val lastReadTime: Long?,
)
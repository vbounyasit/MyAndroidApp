package com.example.mykotlinapp.model.entity.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_event_participants")
data class GroupEventParticipant(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "participant_id")
    val id: Long = 0L,
    @ColumnInfo(name = "parent_event_id")
    val eventId: Long,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String,
)
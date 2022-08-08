package com.example.mykotlinapp.model.entity.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.EventJoinState

@Entity(tableName = "group_events")
data class GroupEvent(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id")
    val id: Long = 0L,
    @ColumnInfo(name = "parent_group_id")
    val groupId: Long,
    @ColumnInfo(name = "event_picture")
    val eventPicture: String,
    @ColumnInfo(name = "event_name")
    val eventName: String,
    @ColumnInfo(name = "event_summary")
    val eventSummary: String,
    @ColumnInfo(name = "event_time")
    val eventTime: String,
    @ColumnInfo(name = "join_state")
    val joinState: EventJoinState,
    @ColumnInfo(name = "participant_count")
    val participantCount: Int,
)
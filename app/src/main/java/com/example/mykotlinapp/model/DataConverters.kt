package com.example.mykotlinapp.model

import androidx.room.TypeConverter
import com.example.mykotlinapp.domain.pojo.ActivityStatus
import com.example.mykotlinapp.domain.pojo.EventJoinState
import com.example.mykotlinapp.domain.pojo.SyncState

/**
 * Data converters for enum types in Room
 */
class DataConverters {

    @TypeConverter
    fun toSyncState(value: Int) = enumValues<SyncState>()[value]

    @TypeConverter
    fun fromSyncState(value: SyncState) = value.ordinal

    @TypeConverter
    fun toEventJoinState(value: Int) = enumValues<EventJoinState>()[value]

    @TypeConverter
    fun fromEventJoinState(value: EventJoinState) = value.ordinal

    @TypeConverter
    fun toActivityStatus(value: Int) = enumValues<ActivityStatus>()[value]

    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus) = value.ordinal

}
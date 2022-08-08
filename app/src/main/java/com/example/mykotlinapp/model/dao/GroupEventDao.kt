package com.example.mykotlinapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mykotlinapp.model.entity.group.GroupEvent
import com.example.mykotlinapp.model.entity.group.GroupEventParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupEventDao {
    /**
     * Create
     */

    @Insert
    suspend fun insert(groupEvent: GroupEvent): Long

    @Insert
    suspend fun insert(groupGroupEventParticipant: GroupEventParticipant): Long

    /**
     * Read
     */

    @Query("SELECT * from group_events WHERE event_id = :key")
    suspend fun getEvent(key: Long): GroupEvent

    /**
     * Update
     */

    @Update
    suspend fun update(groupEvent: GroupEvent)

    @Update
    suspend fun update(groupGroupEventParticipant: GroupEventParticipant)

    /**
     * Delete
     */

    @Query("DELETE from group_events")
    suspend fun clearEvents()

    @Query("DELETE from group_event_participants")
    suspend fun clearEventParticipants()

    /**
     * Flow
     */

    @Query("SELECT * from group_events  LEFT JOIN group_event_participants ON group_events.event_id = group_event_participants.parent_event_id WHERE parent_group_id = :groupId")
    fun getEventsWithParticipants(groupId: Long): Flow<Map<GroupEvent, List<GroupEventParticipant>>>

}
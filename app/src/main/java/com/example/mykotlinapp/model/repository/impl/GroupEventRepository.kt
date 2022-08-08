package com.example.mykotlinapp.model.repository.impl

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.EventJoinState
import com.example.mykotlinapp.model.dao.GroupEventDao
import com.example.mykotlinapp.model.dto.ui.group.GroupEventItemDTO
import com.example.mykotlinapp.model.entity.group.GroupEvent
import com.example.mykotlinapp.model.entity.group.GroupEventParticipant
import com.example.mykotlinapp.model.mappers.impl.group.GroupEventItemMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.max

class GroupEventRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val groupEventDao: GroupEventDao,
) {

    fun getGroupEvents(groupId: Long): Flow<List<GroupEventItemDTO>> =
        groupEventDao.getEventsWithParticipants(groupId)
            .map { eventsWithParticipants ->
                eventsWithParticipants.map { mapEntry ->
                    val groupEventItem = GroupEventItemMapper.toDTO(mapEntry.toPair())
                    val displayedParticipants =
                        groupEventItem.participants.take(context.resources.getInteger(R.integer.event_participant_count_to_display))
                    groupEventItem.copy(
                        participants = displayedParticipants,
                        extraParticipantCount = max(
                            groupEventItem.extraParticipantCount - displayedParticipants.size,
                            0
                        )
                    )
                }
            }

    private suspend fun clear() {
        groupEventDao.clearEventParticipants()
        groupEventDao.clearEvents()
    }

    suspend fun updateEvent(eventId: Long) {
        val event = groupEventDao.getEvent(eventId)
        val newEvent = event.copy(joinState = EventJoinState.DECLINED)
        groupEventDao.update(newEvent)
        groupEventDao.insert(GroupEventParticipant(eventId = newEvent.id, profilePicture = ""))
    }

    suspend fun updateDatabase(groupId: Long) {
        clear()
        val event1 = groupEventDao.insert(
            GroupEvent(
                groupId = groupId,
                eventPicture = "https://images8.alphacoders.com/110/thumb-1920-1102284.jpg",
                eventName = "event one",
                eventSummary = "a great event with all your friends",
                eventTime = "In 5 minutes",
                participantCount = 20,
                joinState = EventJoinState.JOINED
            )
        )
        val event2 = groupEventDao.insert(
            GroupEvent(
                groupId = groupId,
                eventPicture = "https://wallpaperaccess.com/full/22917.jpg",
                eventName = "event two",
                eventSummary = "another great event with all your friends",
                eventTime = "Tomorrow, 5:00 PM",
                participantCount = 25,
                joinState = EventJoinState.MAYBE
            )
        )
        val event3 = groupEventDao.insert(
            GroupEvent(
                groupId = groupId,
                eventPicture = "https://wallpaperaccess.com/full/22917.jpg",
                eventName = "event three",
                eventSummary = "The greatest event of all time",
                eventTime = "March 31st, 7:15 PM",
                participantCount = 35,
                joinState = EventJoinState.DECLINED
            )
        )
        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event1,
                profilePicture = "https://wallpapercave.com/wp/0pNldyU.jpg"
            )
        )
        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event1,
                profilePicture = "https://wallpaperaccess.com/full/22917.jpg"
            )
        )

        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event2,
                profilePicture = "https://wallpaperaccess.com/full/22917.jpg"
            )
        )
        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event2,
                profilePicture = "https://wallpaperaccess.com/full/22917.jpg"
            )
        )
        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event2,
                profilePicture = "https://wallpaperaccess.com/full/22917.jpg"
            )
        )

        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event3,
                profilePicture = "https://wallpaperaccess.com/full/22917.jpg"
            )
        )
        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event3,
                profilePicture = "https://wallpaperaccess.com/full/22917.jpg"
            )
        )
        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event3,
                profilePicture = "https://wallpaperaccess.com/full/22917.jpg"
            )
        )
        groupEventDao.insert(
            GroupEventParticipant(
                eventId = event3,
                profilePicture = "https://wallpapercave.com/wp/0pNldyU.jpg"
            )
        )
    }
}
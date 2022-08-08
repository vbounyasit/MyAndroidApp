package com.example.mykotlinapp.model.mappers.impl.group

import com.example.mykotlinapp.model.dto.ui.group.GroupEventItemDTO
import com.example.mykotlinapp.model.entity.group.GroupEvent
import com.example.mykotlinapp.model.entity.group.GroupEventParticipant
import com.example.mykotlinapp.model.mappers.DTOMapper

object GroupEventItemMapper : DTOMapper<Pair<GroupEvent, List<GroupEventParticipant>>, GroupEventItemDTO> {
    override fun toDTO(entity: Pair<GroupEvent, List<GroupEventParticipant>>): GroupEventItemDTO =
        GroupEventItemDTO(
            "",//entity.first.id, //todo
            entity.first.eventPicture,
            entity.first.eventName,
            entity.first.eventSummary,
            entity.first.eventTime,
            entity.first.joinState,
            entity.second.map { GroupEventParticipantMapper.toDTO(it) },
            entity.first.participantCount
        )
}
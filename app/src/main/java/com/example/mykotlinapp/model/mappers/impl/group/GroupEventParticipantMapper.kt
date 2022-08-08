package com.example.mykotlinapp.model.mappers.impl.group

import com.example.mykotlinapp.model.dto.ui.group.GroupEventParticipantDTO
import com.example.mykotlinapp.model.entity.group.GroupEventParticipant
import com.example.mykotlinapp.model.mappers.DTOMapper

object GroupEventParticipantMapper : DTOMapper<GroupEventParticipant, GroupEventParticipantDTO> {
    override fun toDTO(entity: GroupEventParticipant): GroupEventParticipantDTO =
        GroupEventParticipantDTO(
            "",//entity.id,//todo
            entity.profilePicture
        )
}
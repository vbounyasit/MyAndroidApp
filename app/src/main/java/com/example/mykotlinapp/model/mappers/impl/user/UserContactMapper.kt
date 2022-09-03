package com.example.mykotlinapp.model.mappers.impl.user

import android.content.Context
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.entity.user.UserContact
import com.example.mykotlinapp.model.mappers.DTOMapperWithParam
import com.example.mykotlinapp.model.mappers.NetworkResponseMapper
import com.example.mykotlinapp.model.mappers.impl.Utils
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.SearchContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.UserContactListResponse

object UserContactMapper :
    DTOMapperWithParam<UserContact, UserContactDTO, Context>,
    NetworkResponseMapper<UserContactListResponse, List<UserContact>> {

    override fun toEntity(networkData: UserContactListResponse): List<UserContact> {
        return networkData.sentRequests.map { toEntity(it, ContactRelationType.OUTGOING) } +
                networkData.receivedRequests.map { toEntity(it, ContactRelationType.INCOMING) } +
                networkData.contacts.map { toEntity(it, ContactRelationType.FRIENDS) }
    }

    fun toSearchContactResult(context: Context): (SearchContactResponse) -> List<UserContactDTO> =
        { networkData ->
            val contacts = networkData.contacts.map {
                toDTO(context)(
                    toEntity(
                        it,
                        ContactRelationType.FRIENDS
                    )
                )
            }
            val searchResults = networkData.searchResults.map {
                toDTO(context)(
                    toEntity(
                        it,
                        ContactRelationType.NONE
                    )
                )
            }
            contacts + searchResults
        }

    fun toEntity(
        userContactResponse: UserContactResponse,
        relationType: ContactRelationType
    ): UserContact =
        UserContact(
            userContactResponse.remoteId,
            userContactResponse.firstName,
            userContactResponse.lastName,
            userContactResponse.fullName,
            userContactResponse.profilePicture,
            userContactResponse.description,
            userContactResponse.lastActive,
            relationType,
            SyncState.UP_TO_DATE
        )

    override fun toDTO(parameter: Context): (entity: UserContact) -> UserContactDTO =
        { entity ->
            UserContactDTO(
                entity.remoteId,
                entity.firstName,
                entity.lastName,
                entity.fullName,
                entity.profilePicture,
                entity.description,
                Utils.toActivityStatus(parameter, System.currentTimeMillis(), entity.lastActive),
                entity.relationType
            )
        }
}
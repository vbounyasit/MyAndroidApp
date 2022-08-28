package com.example.mykotlinapp.mappers.impl

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.entity.user.UserContact
import com.example.mykotlinapp.model.mappers.impl.user.UserContactMapper
import com.example.mykotlinapp.network.dto.responses.user.UserResponses.UserContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.SearchContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.UserContactListResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk

class UserContactMapperTest : StringSpec({
    fun getContactResponse(remoteId: String) =
        UserContactResponse(
            remoteId,
            "firstName1",
            "lastName1",
            "fullName1",
            "picture1",
            "description1",
            3000
        )

    data class Result(val remoteId: String, val relationType: ContactRelationType)

    val context = mockk<Context>(relaxed = true)
    every { context.resources.getInteger(R.integer.minutes_to_inactive) } returns 5

    "UserContactMapper.toEntity should return the correct list of contacts" {
        //Given
        fun getContactResponse(remoteId: String) = UserContactResponse(
            remoteId,
            "firstName1",
            "lastName1",
            "fullName1",
            "picture1",
            "description1",
            3000
        )

        val networkResponse = UserContactListResponse(
            sentRequests = listOf(getContactResponse("id1")),
            receivedRequests = listOf(getContactResponse("id2"), getContactResponse("id3")),
            contacts = listOf(getContactResponse("id4"))
        )
        //When
        val resultEntity: List<UserContact> = UserContactMapper.toEntity(networkResponse)

        //Then
        resultEntity.map { Result(it.remoteId, it.relationType) } shouldContainExactly listOf(
            Result("id1", ContactRelationType.OUTGOING),
            Result("id2", ContactRelationType.INCOMING),
            Result("id3", ContactRelationType.INCOMING),
            Result("id4", ContactRelationType.FRIENDS),
        )
    }

    "UserContactMapper.toSearchContactResult should return the correct search results" {
        //Given
        val networkResponse = SearchContactResponse(
            contacts = listOf(getContactResponse("id1"), getContactResponse("id2")),
            searchResults = listOf(getContactResponse("id3"), getContactResponse("id4"))
        )

        //When
        val result: List<UserContactDTO> =
            UserContactMapper.toSearchContactResult(context)(networkResponse)

        //Then
        result.map { Result(it.remoteId, it.relationType) } shouldContainExactly listOf(
            Result("id1", ContactRelationType.FRIENDS),
            Result("id2", ContactRelationType.FRIENDS),
            Result("id3", ContactRelationType.NONE),
            Result("id4", ContactRelationType.NONE)
        )
    }
})

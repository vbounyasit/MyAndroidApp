package com.example.mykotlinapp.mappers.impl

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dto.ui.group.GroupDTO
import com.example.mykotlinapp.model.dto.ui.post.PostGroupData
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.mappers.impl.group.GroupMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GroupMapperTest : StringSpec({
    fun getGroupProperty(remoteId: String) =
        GroupProperty(
            remoteId,
            "chatRemoteId",
            "groupName",
            "",
            null,
            null,
            0,
            null,
            true,
            SyncState.UP_TO_DATE
        )

    data class Result(val remoteId: String, val groupName: String, val groupPictures: List<String>)

    fun GroupDTO.toResult(): Result = Result(remoteId, name, profilePictures)

    val context = mockk<Context>(relaxed = true)
    val defaultGroupName = "defaultGroup"
    every { context.getString(R.string.profile_pictures_delimiter) } returns ", "
    every { context.getString(R.string.default_group_name) } returns defaultGroupName


    "GroupMapper.toDTO should return the correct data" {
        //Given
        val entity = getGroupProperty("remoteId1").copy(groupName = "name1, name2", groupPicture = "picture1, picture2, picture3")
        //When
        val groupDTO = GroupMapper.toDTO(context)(entity)
        //Then
        groupDTO.toResult() shouldBe Result("remoteId1", defaultGroupName, listOf("picture1", "picture2", "picture3"))
    }

    "GroupMapper.toPostGroupData should return the correct data" {
        //Given
        val entity = getGroupProperty("remoteId1").copy(groupPicture = "picture1, picture2, picture3")
        //When
        val postGroupData = GroupMapper.toPostGroupData(context)(entity)
        //Then
        postGroupData shouldBe PostGroupData(entity.groupName, listOf("picture1", "picture2", "picture3"), entity.isAdmin)
    }
})
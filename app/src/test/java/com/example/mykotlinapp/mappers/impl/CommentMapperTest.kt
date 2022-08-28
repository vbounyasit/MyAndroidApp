package com.example.mykotlinapp.mappers.impl

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.model.dto.ui.post.CommentDTO
import com.example.mykotlinapp.model.entity.post.UserComment
import com.example.mykotlinapp.model.mappers.impl.Utils
import com.example.mykotlinapp.model.mappers.impl.comment.CommentMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class CommentMapperTest : StringSpec({
    val context = mockk<Context>(relaxed = true)
    val delimiter = ", "
    every { context.resources.getInteger(R.integer.comments_max_depth) } returns 4
    every { context.getString(R.string.profile_pictures_delimiter) } returns delimiter
    mockkObject(Utils)
    every { Utils.toTimeAgo(any(), any(), any()) } returns "Now"

    fun getUserComment(
        remoteId: String,
        depthLevel: Int,
        isLast: Boolean,
        parentCommentId: String? = null
    ) = UserComment(
        remoteId,
        "parent_post_$remoteId",
        "parent_group_$remoteId",
        parentCommentId,
        "name",
        "picture1",
        "content",
        0,
        null,
        1,
        depthLevel,
        isLast,
        VoteState.NONE,
        true,
        0,
        SyncState.UP_TO_DATE
    )

    data class Result(val remoteId: String, val depthFlags: List<Boolean>)

    fun CommentDTO.toResult() = Result(remoteId, depthFlags.split(delimiter).map(String::toBoolean))

    "CommentMapper.toDTO should return the correct data" {
        //Given
        val userComment = listOf(
            getUserComment("remoteId_1", 0, false),
            getUserComment("remoteId_2", 0, true),
            getUserComment("remoteId_1_1", 1, false, "remoteId_1"),
            getUserComment("remoteId_1_2", 1, true, "remoteId_1"),
            getUserComment("remoteId_1_1_1", 2, false, "remoteId_1_1"),
            getUserComment("remoteId_1_1_2", 2, true, "remoteId_1_1"),
        )
        //When
        val result: List<CommentDTO> = CommentMapper.toDTO(context)(userComment)
        //Then
        result.map(CommentDTO::toResult) shouldContainExactly listOf(
            Result("remoteId_1", listOf(true, true, true, true)),
            Result("remoteId_2", listOf(true, true, true, true)),
            Result("remoteId_1_1", listOf(true, true, true, true)),
            Result("remoteId_1_2", listOf(false, true, true, true)),
            Result("remoteId_1_1_1", listOf(false, true, true, true)),
            Result("remoteId_1_1_2", listOf(false, false, true, true)),
        )
    }
})
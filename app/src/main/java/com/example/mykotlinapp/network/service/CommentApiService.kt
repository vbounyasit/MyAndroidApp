package com.example.mykotlinapp.network.service

import com.example.mykotlinapp.network.ApiService
import com.example.mykotlinapp.network.dto.requests.comment.CreateCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.DeleteCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentVoteRequest
import com.example.mykotlinapp.network.dto.responses.CreateOperationResponse
import com.example.mykotlinapp.network.dto.responses.DeleteOperationResponse
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.post.CommentResponse
import retrofit2.http.*

interface CommentApiService : ApiService {

    @POST("/groups/{groupRemoteId}/posts/{postRemoteId}/comments")
    suspend fun createComment(
        @Header("Authorization") authorization: String,
        @Path("groupRemoteId") groupRemoteId: String,
        @Path("postRemoteId") postRemoteId: String,
        @Body createCommentRequest: CreateCommentRequest,
    ): CreateOperationResponse

    @GET("/groups/{groupRemoteId}/posts/{postRemoteId}/comments")
    suspend fun getCommentList(
        @Header("Authorization") authorization: String,
        @Path("groupRemoteId") groupRemoteId: String,
        @Path("postRemoteId") postRemoteId: String
    ): List<CommentResponse>

    @PATCH("/groups/posts/comments")
    suspend fun updateComments(
        @Header("Authorization") authorization: String,
        @Body updateCommentsRequest: List<UpdateCommentRequest>
    ): UpdateOperationResponse

    @PATCH("/groups/posts/comments/votes")
    suspend fun updateCommentVotes(
        @Header("Authorization") authorization: String,
        @Body updateCommentVotesRequest: List<UpdateCommentVoteRequest>
    ): UpdateOperationResponse

    @HTTP(method = "DELETE", path = "/groups/posts/comments", hasBody = true)
    suspend fun deleteComments(
        @Header("Authorization") authorization: String,
        @Body deleteCommentsRequest: List<DeleteCommentRequest>
    ): DeleteOperationResponse

}
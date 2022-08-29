package com.example.mykotlinapp.network.service

import com.example.mykotlinapp.network.ApiService
import com.example.mykotlinapp.network.dto.requests.post.CreatePostRequest
import com.example.mykotlinapp.network.dto.requests.post.DeletePostRequest
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostRequest
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostVoteRequest
import com.example.mykotlinapp.network.dto.responses.CreateOperationResponse
import com.example.mykotlinapp.network.dto.responses.DeleteOperationResponse
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.post.PostResponse
import retrofit2.http.*

interface PostApiService : ApiService {

    @POST("/groups/{remoteId}/posts")
    suspend fun createPost(@Header("Authorization") authorization: String, @Path("remoteId") groupRemoteId: String, @Body createPostRequest: CreatePostRequest): CreateOperationResponse

    @GET("/groups/{remoteId}/posts")
    suspend fun getPostList(@Header("Authorization") authorization: String, @Path("remoteId") postRemoteId: String): List<PostResponse>

    @GET("/groups/{groupRemoteId}/posts/{postRemoteId}")
    suspend fun getPostData(@Header("Authorization") authorization: String, @Path("groupRemoteId") groupRemoteId: String, @Path("postRemoteId") postRemoteId: String): PostResponse

    @PATCH("/groups/posts")
    suspend fun updatePosts(@Header("Authorization") authorization: String, @Body updatePostsRequest: List<UpdatePostRequest>): UpdateOperationResponse

    @PATCH("/groups/posts/votes")
    suspend fun updatePostVotes(@Header("Authorization") authorization: String, @Body updatePostVotesRequest: List<UpdatePostVoteRequest>): UpdateOperationResponse

    @HTTP(method = "DELETE", path = "/groups/posts", hasBody = true)
    suspend fun deletePosts(@Header("Authorization") authorization: String, @Body deletePostsRequest: List<DeletePostRequest>): DeleteOperationResponse

}
package com.example.mykotlinapp.network

import com.example.mykotlinapp.network.dto.requests.chat.CreateChatLogRequest
import com.example.mykotlinapp.network.dto.requests.chat.CreateChatRequest
import com.example.mykotlinapp.network.dto.requests.chat.UpdateGroupRequest
import com.example.mykotlinapp.network.dto.requests.comment.CreateCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.DeleteCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentRequest
import com.example.mykotlinapp.network.dto.requests.comment.UpdateCommentVoteRequest
import com.example.mykotlinapp.network.dto.requests.post.CreatePostRequest
import com.example.mykotlinapp.network.dto.requests.post.DeletePostRequest
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostRequest
import com.example.mykotlinapp.network.dto.requests.post.UpdatePostVoteRequest
import com.example.mykotlinapp.network.dto.requests.user.LogInUserRequest
import com.example.mykotlinapp.network.dto.requests.user.SignUserUpRequest
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserRequest
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserSettingsRequest
import com.example.mykotlinapp.network.dto.requests.user.contact.CreateContactRequest
import com.example.mykotlinapp.network.dto.requests.user.contact.RemoveContactIdRequest
import com.example.mykotlinapp.network.dto.responses.CreateOperationResponse
import com.example.mykotlinapp.network.dto.responses.DeleteOperationResponse
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.chat.*
import com.example.mykotlinapp.network.dto.responses.post.CommentResponse
import com.example.mykotlinapp.network.dto.responses.post.PostResponse
import com.example.mykotlinapp.network.dto.responses.user.AuthenticatedUserResponse
import com.example.mykotlinapp.network.dto.responses.user.UserLoginResponse
import com.example.mykotlinapp.network.dto.responses.user.UserSignUpResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.CreatedContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.SearchContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.UserContactListResponse
import retrofit2.http.*

interface ApiService {

    /**
     * Users
     */

    @PATCH("/users/login")
    suspend fun logUserIn(@Body logInUserRequest: LogInUserRequest): UserLoginResponse

    @GET("/users/me")
    suspend fun getAuthenticatedUser(@Header("Authorization") authorization: String): AuthenticatedUserResponse

    @PATCH("/users/me")
    suspend fun updateAuthenticatedUser(@Header("Authorization") authorization: String, @Body userUpdateUserRequest: UpdateUserRequest): UpdateOperationResponse

    @POST("/users")
    suspend fun signUserUp(@Body signUserUpRequest: SignUserUpRequest): UserSignUpResponse

    @GET("/users/contacts")
    suspend fun getUserContacts(@Header("Authorization") authorization: String): UserContactListResponse

    @HTTP(method = "DELETE", path = "/users/contacts", hasBody = true)
    suspend fun deleteUserContact(@Header("Authorization") authorization: String, @Body removeUserContactRequest: List<RemoveContactIdRequest>): DeleteOperationResponse

    @POST("/users/contacts")
    suspend fun createUserContactRequest(@Header("Authorization") authorization: String, @Body createContactRequest: CreateContactRequest): CreatedContactResponse

    @GET("/users")
    suspend fun searchContacts(@Header("Authorization") authorization: String, @Query("searchTag") searchTag: String): SearchContactResponse

    @PATCH("/users/settings")
    suspend fun updateUserSettings(@Header("Authorization") authorization: String, @Body updateUserSettingsRequest: UpdateUserSettingsRequest): UpdateOperationResponse

    /**
     * Groups
     */
    @PATCH("/groups")
    suspend fun updateGroups(@Header("Authorization") authorization: String, @Body updateGroupRequest: List<UpdateGroupRequest>): UpdateOperationResponse

    /**
     * Chat
     */

    @POST("/chats")
    suspend fun createChat(@Header("Authorization") authorization: String, @Body createChatRequest: CreateChatRequest): CreateOperationResponse

    @POST("/chats/logs")
    suspend fun createChatLogs(@Header("Authorization") authorization: String, @Body createChatLogsRequest: List<CreateChatLogRequest>): List<CreateOperationResponse>

    @PATCH("/chats/{remoteId}/read")
    suspend fun readChat(@Header("Authorization") authorization: String, @Path("remoteId") chatRemoteId: String): ReadChatResponse

    @GET("/chats")
    suspend fun getChatItems(@Header("Authorization") authorization: String): List<ChatItemResponse>

    @POST("/chats/get")
    suspend fun getChatItems(@Header("Authorization") authorization: String, @Body remoteIds: List<String>): List<ChatItemResponse>

    @GET("/chats/{remoteId}")
    suspend fun getChatData(@Header("Authorization") authorization: String, @Path("remoteId") chatRemoteId: String): ChatResponse

    @GET("/chats/{remoteId}/logs")
    suspend fun getChatBubbles(@Header("Authorization") authorization: String, @Path("remoteId") chatRemoteId: String): ChatBubblesResponse

    @POST("/chats/logs/get")
    suspend fun getNewChatLogs(@Header("Authorization") authorization: String, @Body remoteIds: List<String>): List<ChatLogResponse>

    @POST("/chats/notifications/get")
    suspend fun getNewChatNotifications(@Header("Authorization") authorization: String, @Body chatNotificationRemoteIds: List<String>): List<ChatNotificationResponse>

    /**
     * Post
     */

    @POST("/groups/{remoteId}/posts")
    suspend fun createPost(@Header("Authorization") authorization: String, @Path("remoteId") groupRemoteId: String, @Body createPostRequest: CreatePostRequest): CreateOperationResponse

    @POST("/groups/{groupRemoteId}/posts/{postRemoteId}/comments")
    suspend fun createComment(
        @Header("Authorization") authorization: String,
        @Path("groupRemoteId") groupRemoteId: String,
        @Path("postRemoteId") postRemoteId: String,
        @Body createCommentRequest: CreateCommentRequest,
    ): CreateOperationResponse

    @PATCH("/groups/{remoteId}/read")
    suspend fun readGroup(@Header("Authorization") authorization: String, @Path("remoteId") groupRemoteId: String): ReadGroupResponse

    @GET("/groups/{remoteId}/posts")
    suspend fun getPostList(@Header("Authorization") authorization: String, @Path("remoteId") postRemoteId: String): List<PostResponse>

    @GET("/groups/{groupRemoteId}/posts/{postRemoteId}")
    suspend fun getPostData(@Header("Authorization") authorization: String, @Path("groupRemoteId") groupRemoteId: String, @Path("postRemoteId") postRemoteId: String): PostResponse

    @GET("/groups/{groupRemoteId}/posts/{postRemoteId}/comments")
    suspend fun getCommentList(@Header("Authorization") authorization: String, @Path("groupRemoteId") groupRemoteId: String, @Path("postRemoteId") postRemoteId: String): List<CommentResponse>

    @PATCH("/groups/posts")
    suspend fun updatePosts(@Header("Authorization") authorization: String, @Body updatePostsRequest: List<UpdatePostRequest>): UpdateOperationResponse

    @PATCH("/groups/posts/comments")
    suspend fun updateComments(@Header("Authorization") authorization: String, @Body updateCommentsRequest: List<UpdateCommentRequest>): UpdateOperationResponse

    @PATCH("/groups/posts/votes")
    suspend fun updatePostVotes(@Header("Authorization") authorization: String, @Body updatePostVotesRequest: List<UpdatePostVoteRequest>): UpdateOperationResponse

    @PATCH("/groups/posts/comments/votes")
    suspend fun updateCommentVotes(@Header("Authorization") authorization: String, @Body updateCommentVotesRequest: List<UpdateCommentVoteRequest>): UpdateOperationResponse

    @HTTP(method = "DELETE", path = "/groups/posts", hasBody = true)
    suspend fun deletePosts(@Header("Authorization") authorization: String, @Body deletePostsRequest: List<DeletePostRequest>): DeleteOperationResponse

    @HTTP(method = "DELETE", path = "/groups/posts/comments", hasBody = true)
    suspend fun deleteComments(@Header("Authorization") authorization: String, @Body deleteCommentsRequest: List<DeleteCommentRequest>): DeleteOperationResponse

}
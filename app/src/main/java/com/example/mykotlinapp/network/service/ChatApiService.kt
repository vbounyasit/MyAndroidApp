package com.example.mykotlinapp.network.service

import com.example.mykotlinapp.network.ApiService
import com.example.mykotlinapp.network.dto.requests.chat.CreateChatLogRequest
import com.example.mykotlinapp.network.dto.requests.chat.CreateChatRequest
import com.example.mykotlinapp.network.dto.responses.CreateOperationResponse
import com.example.mykotlinapp.network.dto.responses.chat.*
import retrofit2.http.*

interface ChatApiService : ApiService {

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

}
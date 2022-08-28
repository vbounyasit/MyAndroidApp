package com.example.mykotlinapp.network.service

import com.example.mykotlinapp.network.ApiService
import com.example.mykotlinapp.network.dto.requests.chat.UpdateGroupRequest
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.chat.ReadGroupResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface GroupApiService : ApiService {

    @PATCH("/groups")
    suspend fun updateGroups(
        @Header("Authorization") authorization: String,
        @Body updateGroupRequest: List<UpdateGroupRequest>
    ): UpdateOperationResponse


    @PATCH("/groups/{remoteId}/read")
    suspend fun readGroup(
        @Header("Authorization") authorization: String,
        @Path("remoteId") groupRemoteId: String
    ): ReadGroupResponse

}
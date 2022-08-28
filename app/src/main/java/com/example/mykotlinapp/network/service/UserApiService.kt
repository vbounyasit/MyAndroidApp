package com.example.mykotlinapp.network.service

import com.example.mykotlinapp.network.ApiService
import com.example.mykotlinapp.network.dto.requests.user.LogInUserRequest
import com.example.mykotlinapp.network.dto.requests.user.SignUserUpRequest
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserRequest
import com.example.mykotlinapp.network.dto.requests.user.UpdateUserSettingsRequest
import com.example.mykotlinapp.network.dto.requests.user.contact.CreateContactRequest
import com.example.mykotlinapp.network.dto.requests.user.contact.RemoveContactIdRequest
import com.example.mykotlinapp.network.dto.responses.DeleteOperationResponse
import com.example.mykotlinapp.network.dto.responses.UpdateOperationResponse
import com.example.mykotlinapp.network.dto.responses.user.AuthenticatedUserResponse
import com.example.mykotlinapp.network.dto.responses.user.UserLoginResponse
import com.example.mykotlinapp.network.dto.responses.user.UserSignUpResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.CreatedContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.SearchContactResponse
import com.example.mykotlinapp.network.dto.responses.user.contact.UserContactListResponse
import retrofit2.http.*

interface UserApiService : ApiService {

    @PATCH("/users/login")
    suspend fun logUserIn(@Body logInUserRequest: LogInUserRequest): UserLoginResponse

    @GET("/users/me")
    suspend fun getAuthenticatedUser(@Header("Authorization") authorization: String): AuthenticatedUserResponse

    @PATCH("/users/me")
    suspend fun updateAuthenticatedUser(
        @Header("Authorization") authorization: String,
        @Body userUpdateUserRequest: UpdateUserRequest
    ): UpdateOperationResponse

    @POST("/users")
    suspend fun signUserUp(@Body signUserUpRequest: SignUserUpRequest): UserSignUpResponse

    @GET("/users/contacts")
    suspend fun getUserContacts(@Header("Authorization") authorization: String): UserContactListResponse

    @HTTP(method = "DELETE", path = "/users/contacts", hasBody = true)
    suspend fun deleteUserContact(
        @Header("Authorization") authorization: String,
        @Body removeUserContactRequest: List<RemoveContactIdRequest>
    ): DeleteOperationResponse

    @POST("/users/contacts")
    suspend fun createUserContactRequest(
        @Header("Authorization") authorization: String,
        @Body createContactRequest: CreateContactRequest
    ): CreatedContactResponse

    @GET("/users")
    suspend fun searchContacts(
        @Header("Authorization") authorization: String,
        @Query("searchTag") searchTag: String
    ): SearchContactResponse

    @PATCH("/users/settings")
    suspend fun updateUserSettings(
        @Header("Authorization") authorization: String,
        @Body updateUserSettingsRequest: UpdateUserSettingsRequest
    ): UpdateOperationResponse

}
package com.example.salman3tie.data.api

import com.example.salman3tie.data.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {
    @GET("users")
    suspend fun loginUser(
        @Query("username") usernameFilter: String,
        @Query("password") passwordFilter: String
    ): List<UserModel>

    @POST("users")
    suspend fun registerUser(
        @Body user: UserModel
    ): Response<List<UserModel>>
}

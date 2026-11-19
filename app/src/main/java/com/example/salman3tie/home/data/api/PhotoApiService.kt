package com.example.salman3tie.home.data.api

import com.example.salman3tie.home.data.model.PhotoModel
import retrofit2.http.GET

interface PhotoApiService {
    @GET("list")
    suspend fun getPhotos(): List<PhotoModel>
}
package com.example.salman3tie.home.data.api

import com.example.salman3tie.home.data.model.CatFactModel
import retrofit2.http.GET

interface CatFactApiService {
    @GET("fact")
    suspend fun getCatFact(): CatFactModel
}
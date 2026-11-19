package com.example.salman3tie.home.data.api

import com.example.salman3tie.home.data.model.CatFactModel

interface CatFactApiService {
    @GET("fact")
    suspend fun getCatFact(): CatFactModel
}
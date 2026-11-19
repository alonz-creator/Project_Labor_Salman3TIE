package com.example.salman3tie.home.data.api

object CatFactApiClient {
    private const val BASE_URL = "https://catfact.ninja"

    val apiService: CatFactApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatFactApiService::class.java)
    }
}
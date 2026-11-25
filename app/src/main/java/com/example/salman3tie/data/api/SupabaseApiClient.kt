package com.example.salman3tie.data.api

import com.example.salman3tie.data.supabase.SupabaseConfig
import com.example.salman3tie.data.supabase.SupabaseInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseApiClient {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(SupabaseInterceptor())
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SupabaseConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val noteApiService: NoteApiService by lazy {
        retrofit.create(NoteApiService::class.java)
    }

    val productApiService: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }
}

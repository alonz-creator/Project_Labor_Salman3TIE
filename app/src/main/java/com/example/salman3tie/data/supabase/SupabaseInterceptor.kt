package com.example.salman3tie.data.supabase

import okhttp3.Interceptor
import okhttp3.Response

class SupabaseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", SupabaseConfig.SUPABASE_KEY)
            .header("Authorization", "Bearer ${SupabaseConfig.SUPABASE_KEY}")
            .header("Accept-Profile", SupabaseConfig.SCHEMA)
            .header("Content-Profile", SupabaseConfig.SCHEMA)
            .header("Prefer", "return=representation")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

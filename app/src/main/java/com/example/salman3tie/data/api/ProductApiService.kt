package com.example.salman3tie.data.api

import com.example.salman3tie.data.model.ProductModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductApiService {
    @GET("product")
    suspend fun getProducts(): List<ProductModel>

    @POST("product")
    suspend fun createProduct(
        @Body product: ProductModel
    ): Response<List<ProductModel>>

    @PATCH("product")
    suspend fun updateProduct(
        @Query("id") idFilter: String,
        @Body product: ProductModel
    ): Response<List<ProductModel>>

    @DELETE("product")
    suspend fun deleteProduct(
        @Query("id") idFilter: String
    ): Response<ResponseBody>
}

package com.example.salman3tie.data.api

import com.example.salman3tie.data.model.NoteModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface NoteApiService {
    @GET("note")
    suspend fun getNotes(): List<NoteModel>

    @POST("note")
    suspend fun createNote(
        @Body note: NoteModel
    ): Response<List<NoteModel>>

    @PATCH("note")
    suspend fun updateNote(
        @Query("id") idFilter: String,
        @Body note: NoteModel
    ): Response<List<NoteModel>>

    @DELETE("note")
    suspend fun deleteNote(
        @Query("id") idFilter: String
    ): Response<ResponseBody>
}

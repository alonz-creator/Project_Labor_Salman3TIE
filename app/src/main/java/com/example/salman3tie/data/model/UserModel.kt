package com.example.salman3tie.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    val id: Long? = null,
    @SerializedName("username")
    val username: String,
    val email: String? = null,
    val password: String,
    val name: String,
    val created_at: String? = null
)

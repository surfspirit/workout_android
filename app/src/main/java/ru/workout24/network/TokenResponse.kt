package ru.workout24.network

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("token")
    var token: String,
    @SerializedName("user")
    var user: User
)
package ru.workout24.network

import com.google.gson.annotations.SerializedName

data class CreatePushTokenBody(
    @SerializedName("token")
    val token: String?
)
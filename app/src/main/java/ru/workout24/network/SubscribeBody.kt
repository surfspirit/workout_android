package ru.workout24.network

import com.google.gson.annotations.SerializedName

data class SubscribeBody (
    @SerializedName("expired_at")
    val expired_at: String,
    @SerializedName("external_id")
    val external_id: String
)

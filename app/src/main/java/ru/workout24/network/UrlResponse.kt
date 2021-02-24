package ru.workout24.network

import com.google.gson.annotations.SerializedName

data class UrlResponse(
    @SerializedName("url")
    var url: String?
)
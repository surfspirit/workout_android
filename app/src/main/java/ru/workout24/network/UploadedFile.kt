package ru.workout24.network

import com.google.gson.annotations.SerializedName

data class UploadedFile(
    @SerializedName("url")
    val url: String
)
package ru.workout24.network

import com.google.gson.annotations.SerializedName

data class FilterQueryParam (
    @SerializedName("condition")
    val condition: String?,
    @SerializedName("data")
    val data: Any?
)
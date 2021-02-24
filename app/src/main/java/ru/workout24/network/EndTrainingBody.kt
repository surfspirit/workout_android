package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class EndTrainingBody(
    @SerializedName("difficulty")
    val difficulty: String?,
    @SerializedName("like")
    val like: Boolean?
)
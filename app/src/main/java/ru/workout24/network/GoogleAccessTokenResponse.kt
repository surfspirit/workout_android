package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class GoogleAccessTokenResponse(
    @SerializedName("access_token")
    var access_token: String?
)
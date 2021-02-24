package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class GoogleAccessTokenBody(
    @SerializedName("grant_type")
    var grant_type: String?,
    @SerializedName("client_id")
    var client_id: String?,
    @SerializedName("client_secret")
    var client_secret: String?,
    @SerializedName("code")
    var code: String?
)
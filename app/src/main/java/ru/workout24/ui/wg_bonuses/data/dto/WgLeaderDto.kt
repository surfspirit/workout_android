package ru.workout24.ui.wg_bonuses.data.dto


import com.google.gson.annotations.SerializedName

data class WgLeaderDto(
    @SerializedName("avatar_url")
    var avatarUrl: String?,
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String?,
    @SerializedName("place")
    var place: Int,
    @SerializedName("surname")
    var surname: String?,
    @SerializedName("value")
    var value: Int
)
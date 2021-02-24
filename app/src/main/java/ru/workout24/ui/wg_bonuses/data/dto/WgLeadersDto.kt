package ru.workout24.ui.wg_bonuses.data.dto


import com.google.gson.annotations.SerializedName

data class WgLeadersDto(
    @SerializedName("data")
    var data: List<WgLeaderDto>,
    @SerializedName("has_more")
    var hasMore: Boolean,
    @SerializedName("limit")
    var limit: Int,
    @SerializedName("page")
    var page: Int,
    @SerializedName("total")
    var total: Int
)
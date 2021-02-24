package ru.workout24.ui.wg_bonuses_detail.data.dto


import com.google.gson.annotations.SerializedName

data class WgLeaderDetailDto(
    @SerializedName("level")
    var level: Int,
    @SerializedName("place")
    var place: Int,
    @SerializedName("today_wg")
    var todayWg: Int,
    @SerializedName("total_wg")
    var totalWg: Int,
    @SerializedName("wg_next_level")
    var wgNextLevel: Int
)
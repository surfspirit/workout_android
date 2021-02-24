package ru.workout24.ui.statistics_layer.statistics.dto

import com.google.gson.annotations.SerializedName
import ru.workout24.network.UserStatistic

data class UserStatsPageDto(
    @SerializedName("total")
    val total:Int,
    @SerializedName("limit")
    val limit:Int,
    @SerializedName("page")
    val page:Int,
    @SerializedName("has_more")
    val hasMore:Boolean,
    @SerializedName("data")
    val data:List<UserStatistic>
)
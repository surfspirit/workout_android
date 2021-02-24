package ru.workout24.ui.statistics_layer.statistics.dto

import com.google.gson.annotations.SerializedName

data class UserStatsQueryDto(
    @SerializedName("page")
    var page:Int = 1,
    @SerializedName("limit")
    var limit:Int = 30,
    @SerializedName("day")
    var day:String = ""
);
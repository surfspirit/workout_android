package ru.workout24.ui.statistics_layer.statistics.dto

import com.google.gson.annotations.SerializedName

data class UserStatsDatesQuery(
    @SerializedName("limit")
    var limit: Int = 30,
    @SerializedName("page")
    var page: Int = 1
)
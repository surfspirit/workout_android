package ru.workout24.ui.statistics_layer.statistics.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class UserStatsDatesDto(
    @SerializedName("total")
    val total: Int,
    @SerializedName("data")
    val data: List<String>?,
    @SerializedName("limit")
    val limit: Int?,
    @SerializedName("page")
    val page: Int?,
    @SerializedName("has_more")
    val hasMore: Boolean
)

@Entity
data class UserStatsDate(
    @PrimaryKey
    val date:String
)

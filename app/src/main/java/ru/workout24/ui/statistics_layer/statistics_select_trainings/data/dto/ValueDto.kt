package ru.workout24.ui.statistics_layer.statistics_select_trainings.data.dto


import com.google.gson.annotations.SerializedName

data class ValueDto(
    @SerializedName("condition")
    val condition: String?,
    @SerializedName("data")
    val valueData: Any?
)
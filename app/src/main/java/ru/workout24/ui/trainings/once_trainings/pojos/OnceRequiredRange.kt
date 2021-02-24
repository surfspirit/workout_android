package ru.workout24.ui.trainings.once_trainings.pojos


import com.google.gson.annotations.SerializedName

data class OnceRequiredRange(
    @SerializedName("max")
    val max: Int?,
    @SerializedName("min")
    val min: Int?
)
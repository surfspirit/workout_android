package ru.workout24.ui.lk_layer.edit_profile.data.dto


import com.google.gson.annotations.SerializedName

data class ErrorsDto(
    @SerializedName("notifications")
    val notifications: String?
)
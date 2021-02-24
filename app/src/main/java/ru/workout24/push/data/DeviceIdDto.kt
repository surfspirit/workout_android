package ru.workout24.push.data

import com.google.gson.annotations.SerializedName

data class DeviceIdDto(
    @SerializedName("device_id")
    val deviceId: String
)
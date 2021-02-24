package ru.workout24.network

import com.google.gson.annotations.SerializedName

class TimezoneBody(
    @SerializedName("time_zone")
    val timeZone: String
)

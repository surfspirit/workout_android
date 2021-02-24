package ru.workout24.push.data


import android.app.PendingIntent
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel

data class PushBodyDto(
    @SerializedName("badge")
    var badge: String? = null,
    @SerializedName("click_action")
    var clickAction: String? = null,
    @SerializedName("color")
    var color: String? = null,
    @SerializedName("data_message")
    var dataMessage: Map<String, String>? = null,
    @SerializedName("low_priority")
    var lowPriority: Boolean,
    @SerializedName("message_body")
    var messageBody: String,
    @SerializedName("message_icon")
    var messageIcon: String? = null,
    @SerializedName("message_title")
    var messageTitle: String,
    @SerializedName("sound")
    var sound: String? = null,
    @SerializedName("tag")
    var tag: String? = null,
    @IgnoredOnParcel
    var pendingIntent: PendingIntent
)

class DataMessageDto(
)
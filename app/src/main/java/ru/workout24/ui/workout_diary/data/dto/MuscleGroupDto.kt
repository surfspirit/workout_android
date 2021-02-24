package ru.workout24.ui.workout_diary.data.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MuscleGroupDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("preview_url")
    val previewUrl: String?
): Parcelable
package ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatisticsEntryDto(
    @SerializedName("arm_size")
    val armSize: Int?,
    @SerializedName("chest_size")
    val chestSize: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("fat_percentage")
    val fatPercentage: Int?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("id")
    val id: String,
    @SerializedName("neck_size")
    val neckSize: Int?,
    @SerializedName("photos_url")
    val photosUrl: ArrayList<String>?,
    @SerializedName("target")
    val target: TargetDto?,
    @SerializedName("target_type")
    val targetType: String?,
    @SerializedName("thigh_size")
    val thighSize: Int?,
    @SerializedName("waist_size")
    val waistSize: Int?,
    @SerializedName("weight")
    val weight: Int?
): Parcelable
package ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto


import com.google.gson.annotations.SerializedName

data class StatisticsEntryRequestDto(
    @SerializedName("arm_size")
    val armSize: Int?,
    @SerializedName("chest_size")
    val chestSize: Int?,
    @SerializedName("fat_percentage")
    val fatPercentage: Int?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("neck_size")
    val neckSize: Int?,
    @SerializedName("photos_url")
    val photosUrl: List<String?>?,
    @SerializedName("target_id")
    val targetId: String?,
    @SerializedName("target_type")
    val targetType: String?,
    @SerializedName("thigh_size")
    val thighSize: Int?,
    @SerializedName("waist_size")
    val waistSize: Int?,
    @SerializedName("weight")
    val weight: Int?
)
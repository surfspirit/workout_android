package ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto


import com.google.gson.annotations.SerializedName
import ru.workout24.network.StatisticTarget
import ru.workout24.network.UserStatistic

data class StatisticsEntryResponseDto(
    @SerializedName("arm_size")
    val armSize: Int?,
    @SerializedName("chest_size")
    val chestSize: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("fat_percentage")
    val fatPercentage: Float?,
    @SerializedName("height")
    val height: Float?,
    @SerializedName("id")
    val id: String,
    @SerializedName("neck_size")
    val neckSize: Float?,
    @SerializedName("photos_url")
    val photosUrl: List<String?>?,
    @SerializedName("target")
    val target: TargetDto?,
    @SerializedName("target_type")
    val targetType: String?,
    @SerializedName("thigh_size")
    val thighSize: Float?,
    @SerializedName("waist_size")
    val waistSize: Float?,
    @SerializedName("weight")
    val weight: Float?
) {
    fun convertToUserStatistic(photosUrl: List<String>? = null): UserStatistic {
        return UserStatistic(
            chestSize = chestSize?.toFloat(),
            targetType = targetType,
            createdAt = createdAt,
            weight = weight?.toFloat(),
            armSize = armSize?.toFloat(),
            target = StatisticTarget(target?.name!!, target.id!!),
            fatPercentage = fatPercentage?.toFloat(),
            neckSize = neckSize?.toFloat(),
            thighSize = thighSize?.toFloat(),
            id = id,
            waistSize = waistSize?.toFloat(),
            photosUrl = this.photosUrl?.filterNotNull() ?: photosUrl,
            height = height?.toFloat(),
            createdDay = null
        )
    }
}
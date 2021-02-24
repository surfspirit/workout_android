package ru.workout24.network


import android.os.Parcelable
import android.text.TextUtils
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatisticTarget(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: String
): Parcelable

@Entity
@TypeConverters(StatisticTargetConverter::class, StringListConverter::class)
@Parcelize
data class UserStatistic(
    @SerializedName("chest_size")
    val chestSize: Float?,
    @SerializedName("target_type")
    val targetType: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("weight")
    val weight: Float?,
    @SerializedName("arm_size")
    val armSize: Float?,
    @SerializedName("target")
    val target: StatisticTarget?,
    @SerializedName("fat_percentage")
    val fatPercentage: Float?,
    @SerializedName("neck_size")
    val neckSize: Float?,
    @SerializedName("thigh_size")
    val thighSize: Float?,
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("waist_size")
    val waistSize: Float?,
    @SerializedName("photos_url")
    val photosUrl: List<String>?,
    @SerializedName("height")
    val height: Float?,
    var createdDay: String?
): Parcelable

class StatisticTargetConverter {
    @TypeConverter
    fun stringToTarget(data: String?): StatisticTarget? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<StatisticTarget?>() {}.type

        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun targetToString(obj: StatisticTarget?): String {
        return Gson().toJson(obj)
    }
}

class StringListConverter {

    @TypeConverter
    fun fromListString(strings: List<String?>): String? {
        return TextUtils.join(",", strings)
    }

    @TypeConverter
    fun toListString(string: String): List<String> {
        return string.split(",")

    }

}

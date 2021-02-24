package ru.workout24.ui.workout_diary.data.dto


import android.os.Parcelable
import androidx.room.*
import ru.workout24.utills.calendarFrom_ISO
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "DiarySingleExercise")
@TypeConverters(SingleExerciseDataDtoConverter::class)
data class SingleExerciseDto(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("exercise")
    val singleExerciseData: SingleExerciseDataDto?,
    @SerializedName("id")
    @PrimaryKey
    val id: String,
    @SerializedName("time")
    val time: Int?,
    @SerializedName("start_date")
    val startDate: String?,
    var month: Int? = null,
    var year: Int? = null
) : Parcelable, DateSortalable {
    override fun getDate(): Calendar = startDate.calendarFrom_ISO()
}

@Parcelize
data class SingleExerciseDataDto(
    @SerializedName("author_id")
    val authorId: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("inventories")
    val inventories: List<InventoryDto>?,
    @SerializedName("muscle_groups")
    val muscleGroups: List<MuscleGroupDto>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("preview_url")
    val previewUrl: String?,
    @SerializedName("video_url")
    val videoUrl: String?,
    @SerializedName("required_range")
    @Embedded(prefix = "range")
    val requiredRange: RequiredRange?,
    @SerializedName("pause_time")
    var pauseTime: Int?,
    @SerializedName("estimate_time")
    var estimate_time: Int?
) : Parcelable

class SingleExerciseDataDtoConverter {
    @TypeConverter
    fun toString(data: SingleExerciseDataDto): String {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun fromString(data: String): SingleExerciseDataDto {
        return Gson().fromJson(data, SingleExerciseDataDto::class.java)
    }
}

@Parcelize
data class RequiredRange(
    @SerializedName("max")
    val max: Int?,
    @SerializedName("min")
    val min: Int?
) : Parcelable
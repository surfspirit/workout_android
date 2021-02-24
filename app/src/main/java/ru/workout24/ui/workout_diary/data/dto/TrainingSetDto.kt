package ru.workout24.ui.workout_diary.data.dto


import android.os.Parcelable
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import ru.workout24.utills.doIf
import java.util.*

@Parcelize
@Entity(tableName = "DiaryTrainingSet")
@TypeConverters(TrainingDtoConverter::class)
data class TrainingSetDto(
    @SerializedName("available")
    val available: Boolean?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("goals")
    val goals: String?,
    @SerializedName("id")
    @PrimaryKey
    val id: String,
    @SerializedName("inventories")
    val inventories: List<InventoryDto>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("preview_url")
    val previewUrl: String?,
    @SerializedName("training_level")
    val trainingLevel: String?,
    @SerializedName("trainings")
    val trainings: List<TrainingDto>?,
    @SerializedName("trainings_total")
    val trainingsTotal: Int?,
    @SerializedName("weeks_count")
    val weeksCount: Int?,
    var month: Int? = null,
    var year: Int? = null
) : Parcelable, DateSortalable {
    @Ignore
    @IgnoredOnParcel
    // если ни одна тренировка не выполняется в программе, то программа завершена
    val isDone = trainings?.find { it.status == TrainingStatus.SCHEDULED } == null

    @Ignore
    @IgnoredOnParcel
    val trainingsWithoutCanceledAndSkipped =
        trainings
            ?.doIf(isDone) { filter { it.status != TrainingStatus.SKIPPED } } // если программа завершена, то все скипнутые тренировки не выводим
            ?.filter { it.status != TrainingStatus.CANCELED } // не выводим отмененные тренировки

    @Ignore
    @IgnoredOnParcel
    val trainingsSortedByDate = getTrainingsSortedBy { getDate() }

    @Ignore
    @IgnoredOnParcel
    // коллекция отфильтованных тренировок
    // нужен если calendarMode == week
    var filterTrainings: MutableList<TrainingDto> = mutableListOf()

    @IgnoredOnParcel
    val startTime get() = trainings?.firstOrNull()?.startDate

    fun getTrainingsSortedBy(selector: (TrainingDto) -> Comparable<*>?): List<TrainingDto> {
        return trainingsWithoutCanceledAndSkipped?.sortedWith(compareBy(selector))?.reversed() ?: emptyList()
    }

    //начало программы -- это дата первой тренировки
    override fun getDate(): Calendar = trainings?.firstOrNull()?.getDate() ?: Calendar.getInstance()
}

class TrainingDtoConverter {
    @TypeConverter
    fun toString(data: List<TrainingDto>): String {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun fromString(data: String): List<TrainingDto> {
        val type = object : TypeToken<List<TrainingDto>>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun inventoryToString(data: List<InventoryDto>): String {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun inventoryFromString(data: String): List<InventoryDto> {
        val type = object : TypeToken<List<InventoryDto>>() {}.type
        return Gson().fromJson(data, type)
    }
}
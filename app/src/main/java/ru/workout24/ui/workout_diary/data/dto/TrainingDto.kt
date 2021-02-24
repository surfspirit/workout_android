package ru.workout24.ui.workout_diary.data.dto


import android.os.Parcelable
import androidx.room.*
import ru.workout24.utills.calendarFrom_ISO
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "DiaryTraining")
@TypeConverters(TrainingDataDtoConverter::class, StatusAndTypeConverter::class)
data class TrainingDto(
    @SerializedName("assignment_id")
    @PrimaryKey
    val assignmentId: String,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("status")
    val status: TrainingStatus?,
    @SerializedName("training")
    val trainingData: TrainingDataDto?,
    @SerializedName("type")
    val type: TrainingType?,
    var month: Int? = null,
    var year: Int? = null,
    @Embedded
    var programData: ProgramData? = null
) : Parcelable, DateSortalable {
    override fun getDate(): Calendar = startDate.calendarFrom_ISO()
}

@Parcelize
data class ProgramData(
    var programId: String?,
    var programStartDate: String?,
    var programName: String?,
    var programDuration: Int?,
    var isProgramDone: Boolean = false
) : Parcelable

enum class TrainingStatus {
    SCHEDULED,
    STARTED,
    COMPLETED,
    SKIPPED,
    CANCELED
}

enum class TrainingType {
    BY_USER, BY_TRAINER, BY_TRAINING_SET
}

class StatusAndTypeConverter() {
    @TypeConverter
    fun statusFromString(data: String): TrainingStatus = TrainingStatus.valueOf(data)

    @TypeConverter
    fun typeFromString(data: String): TrainingType = TrainingType.valueOf(data)

    @TypeConverter
    fun statusToString(data: TrainingStatus): String = data.toString()

    @TypeConverter
    fun typeFromString(data: TrainingType): String = data.toString()
}

@Parcelize
data class TrainingDataDto(
    @SerializedName("available")
    val available: Boolean?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("estimate_duration")
    val estimateDuration: Int?,
    @SerializedName("exercises_total")
    val exercisesTotal: Int?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("goals")
    val goals: String?,
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
    @SerializedName("sets_count")
    val setsCount: Int?,
    @SerializedName("training_level")
    val trainingLevel: String?
) : Parcelable

class TrainingDataDtoConverter {
    @TypeConverter
    fun toString(data: TrainingDataDto): String {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun fromString(data: String): TrainingDataDto {
        return Gson().fromJson(data, TrainingDataDto::class.java)
    }
}
package ru.workout24.ui.trainings.training_programs.pojos


import android.os.Parcelable
import androidx.room.*
import ru.workout24.ui.trainings.once_exercise.Inventories
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@TypeConverters(
    ExerciseTrainingProgram.MuscleGroupConverter::class,
    ExerciseTrainingProgram.InventoriesConverter::class
)
@Parcelize
data class ExerciseTrainingProgram(
    @SerializedName("description")
    val description: String?,
    @SerializedName("estimate_time")
    val estimateTime: Int?,
    @SerializedName("exercise_id")
    val exerciseId: String?,
    @SerializedName("id")
    @PrimaryKey
    val id: String,
    @SerializedName("inventories")
    val inventory: List<Inventories>?,
    @SerializedName("muscle_groups")
    val muscle_groups: List<MuscleGroup>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("pause_time")
    val pauseTime: Int?,
    @SerializedName("preview_url")
    val previewUrl: String?,

    @SerializedName("required_range")
    @Embedded(prefix = "range")
    val requiredRange: RequiredRange?,
    @SerializedName("video_url")
    val videoUrl: String?
): Parcelable {
    val isTimeExercise: Boolean get() = requiredRange?.units == UNITS.SECOND

    val repeatCount: Int get() = requiredRange?.max?.plus(10) ?: 10

    fun toExerciseTrainingProgramWithPosition(position: Int): ExerciseTrainingProgramWithPosition
            = ExerciseTrainingProgramWithPosition(position, this)

    class InventoriesConverter {
        @TypeConverter
        fun stringToList(data: String?): List<Inventories?> {
            if (data == null) {
                return Collections.emptyList()
            }
            val listType = object : TypeToken<List<Inventories?>>() {}.type

            return Gson().fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(someObjects: List<Inventories?>): String {
            return Gson().toJson(someObjects)
        }
    }

    class MuscleGroupConverter {
        @TypeConverter
        fun stringToList(data: String?): List<MuscleGroup?> {
            if (data == null) {
                return Collections.emptyList()
            }
            val listType = object : TypeToken<List<MuscleGroup?>>() {}.type

            return Gson().fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(someObjects: List<MuscleGroup?>): String {
            return Gson().toJson(someObjects)
        }
    }

    @Ignore
    var setTitle: String? = null
    @Ignore
    var setDesc: String? = null
}

class ExerciseTrainingProgramWithPosition(
    val position: Int,
    val exerciseTrainingProgram: ExerciseTrainingProgram
)
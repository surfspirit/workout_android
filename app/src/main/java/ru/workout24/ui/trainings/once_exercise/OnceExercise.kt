package ru.workout24.ui.trainings.once_exercise


import androidx.room.*
import ru.workout24.ui.trainings.training_programs.pojos.ExerciseTrainingProgram
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import ru.workout24.ui.trainings.training_programs.pojos.RequiredRange
import java.util.*

@Entity
@TypeConverters(MuscleGroupConverter::class, InventoriesConverter::class)
data class OnceExercise(
    @SerializedName("available")
    var available: Boolean?,
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
) {
    @Ignore
    var setTitle: String? = null
    @Ignore
    var setDesc: String? = null
}

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


fun OnceExercise.toExerciseTrainingProgram() = ExerciseTrainingProgram(
    id = id,
    name = name,
    description = description,
    inventory = inventory,
    estimateTime = estimateTime,
    exerciseId = exerciseId,
    pauseTime = pauseTime,
    previewUrl = previewUrl,
    requiredRange = RequiredRange(
        requiredRange?.max,
        requiredRange?.min,
        requiredRange?.units
    ),
    videoUrl = videoUrl,
    muscle_groups = muscle_groups

)
package ru.workout24.ui.trainings.training_programs.pojos


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.workout24.ui.trainings.once_exercise.Inventories
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.*


@Entity
@TypeConverters(WeekConverter::class,StringListConverter::class,ExerciseTrainingProgram.InventoriesConverter::class)
data class TrainingProgram(
    @SerializedName("available")
    val available: Boolean?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("exercises_total")
    val exercisesTotal: Int?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("goals")
    val goals: String?,
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("inventories")
    val inventories: List<Inventories?>,
    @SerializedName("name")
    val name: String?,
    @SerializedName("preview_url")
    val previewUrl: String?,
    @SerializedName("training_level")
    val trainingLevel: String?,
    @SerializedName("trainings_per_week")
    val trainingsPerWeek: Int?,
    @SerializedName("trainings_total")
    val trainingsTotal: Int?,
    @SerializedName("weeks_count")
    val weeks_count: Int?,
    @SerializedName("weeks")
    val weeks: List<Week?>
)

class WeekConverter {
    @TypeConverter
    fun stringToList(data: String?): List<Week?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Week?>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<Week?>): String {
        return Gson().toJson(someObjects)
    }
}


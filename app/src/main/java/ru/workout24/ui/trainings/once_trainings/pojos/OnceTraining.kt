package ru.workout24.ui.trainings.once_trainings.pojos


import android.text.TextUtils
import androidx.room.*
import ru.workout24.ui.trainings.once_exercise.Inventories
import ru.workout24.ui.trainings.once_exercise.InventoriesConverter
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import ru.workout24.ui.trainings.once_exercise.MuscleGroupConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.*

@Entity
@TypeConverters(StringListConverter::class,OnceSetConverter::class,InventoriesConverter::class,MuscleGroupConverter::class)
data class OnceTraining(
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
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("inventories")
    val inventories: List<Inventories?>,
    @SerializedName("muscle_groups")
    val muscleGroups: List<MuscleGroup?>,
    @SerializedName("name")
    val name: String?,
    @SerializedName("preview_url")
    val previewUrl: String?,
    @SerializedName("sets_count")
    val setsCount: Int?,
    @SerializedName("training_level")
    val trainingLevel: String?
)
{
    
    @Ignore
    var weekTitle: String? = null
    @Ignore
    var weekDesc: String? = null
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
class OnceSetConverter {
    @TypeConverter
    fun stringToList(data: String?): List<OnceSet?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<OnceSet?>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<OnceSet?>): String {
        return Gson().toJson(someObjects)
    }
}
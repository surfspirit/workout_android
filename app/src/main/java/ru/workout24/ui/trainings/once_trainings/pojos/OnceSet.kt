package ru.workout24.ui.trainings.once_trainings.pojos


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.*
import ru.workout24.ui.trainings.once_exercise.OnceExercise

@Entity
@TypeConverters(OnceExerciseConverter::class)
data class OnceSet(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @SerializedName("exercises")
    val exercises: List<OnceExercise?>?
)
class OnceExerciseConverter {
    @TypeConverter
    fun stringToList(data: String?): List<OnceExercise?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<OnceExercise?>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<OnceExercise?>): String {
        return Gson().toJson(someObjects)
    }
}
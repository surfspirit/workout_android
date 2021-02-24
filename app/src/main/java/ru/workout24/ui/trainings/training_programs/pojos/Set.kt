package ru.workout24.ui.trainings.training_programs.pojos


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@TypeConverters(ExerciseTrainingProgramConverter::class)
@Parcelize
data class Set(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @SerializedName("exercises")
    val exercises: List<ExerciseTrainingProgram>
) : Parcelable {
    fun toSetWithExercisesQueue(position: Int): SetWithExercisesQueue =
        SetWithExercisesQueue(
            position,
            exercises.size,
            ArrayDeque(exercises.mapIndexed { index, exercise ->
                exercise.toExerciseTrainingProgramWithPosition(index)
            })
        )
}

class SetWithExercisesQueue(
    val position: Int,
    val allExercises: Int,
    val exercisesQueue: ArrayDeque<ExerciseTrainingProgramWithPosition>
) {
    val exercisesCount get() = exercisesQueue.size
    val currentExercise get() = exercisesQueue.first
    val isEmpty get() = exercisesQueue.isNullOrEmpty()

    fun next() {
        if (exercisesQueue.isNotEmpty()) exercisesQueue.removeFirst()
    }
}

class ExerciseTrainingProgramConverter {
    @TypeConverter
    fun stringToList(data: String?): List<ExerciseTrainingProgram?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<ExerciseTrainingProgram?>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<ExerciseTrainingProgram?>): String {
        return Gson().toJson(someObjects)
    }
}
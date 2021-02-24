package ru.workout24.ui.trainings.training_programs.pojos


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.*

@Entity
@TypeConverters(TrainingConverter::class)
data class Week(
    @PrimaryKey(autoGenerate = true)
    val id: Int,


    @SerializedName("trainings")
    val trainings: List<Training?>
)
class TrainingConverter {
    @TypeConverter
    fun stringToList(data: String?): List<Training?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Training?>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<Training?>): String {
        return Gson().toJson(someObjects)
    }
}
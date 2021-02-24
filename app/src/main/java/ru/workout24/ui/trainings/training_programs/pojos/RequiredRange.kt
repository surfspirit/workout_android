package ru.workout24.ui.trainings.training_programs.pojos


import android.os.Parcelable
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@TypeConverters(UnitsConverter::class)
@Parcelize
data class RequiredRange(
    @SerializedName("max")
    val max: Int?,
    @SerializedName("min")
    val min: Int?,
    @SerializedName("units")
    val units: UNITS?
) : Parcelable

enum class UNITS {
    COUNT,
    SECOND,
    METER,
    KILOGRAM
}

class UnitsConverter {
    @TypeConverter
    fun convertToString(value: UNITS): String = value.toString()

    @TypeConverter
    fun convertFromString(value: String): UNITS = UNITS.valueOf(value)
}
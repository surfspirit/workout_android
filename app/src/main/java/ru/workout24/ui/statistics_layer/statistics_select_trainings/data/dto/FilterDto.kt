package ru.workout24.ui.statistics_layer.statistics_select_trainings.data.dto


import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.reactivex.Single

@Entity(tableName = "Filter")
@TypeConverters(FilterValueConverter::class)
data class FilterDto(
    @SerializedName("condition")
    val condition: String?,
    @SerializedName("field")
    val field: String?,
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("target")
    val target: String?,
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: Any
)

@Dao
interface DaoFilters {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<FilterDto>)


    @Query("""SELECT * from Filter """)
    fun getAll(): Single<List<FilterDto>>
}

class FilterValueConverter {
    @TypeConverter
    fun convertToString(value: Any): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun convertToObject(value: String): Any {
        return Gson().fromJson<Any>(value, Any::class.java)
    }
}
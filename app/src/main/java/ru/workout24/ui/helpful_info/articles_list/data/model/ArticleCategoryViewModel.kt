package ru.workout24.ui.helpful_info.articles_list.data.model

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

@Parcelize
@Entity
@TypeConverters(ArticleConverter::class)
data class ArticleCategoryViewModel(
    @SerializedName("title")
    val title: String,
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("articles")
    val articles: List<ArticleViewModel>
) :  Parcelable

class ArticleConverter {
    @TypeConverter
    fun stringToList(data: String?): List<ArticleViewModel?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<ArticleViewModel?>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<ArticleViewModel?>): String {
        return Gson().toJson(someObjects)
    }
}
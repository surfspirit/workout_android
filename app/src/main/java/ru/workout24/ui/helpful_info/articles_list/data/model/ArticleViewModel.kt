package ru.workout24.ui.helpful_info.articles_list.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

val ARTICLE_TYPE = 11

@Entity
@Parcelize
data class ArticleViewModel(
    @SerializedName("img_url")
    val photoUrl: String?,
    @SerializedName("title")
    val title: String,
    @SerializedName("text")
    val text: String?,
    @PrimaryKey
    @SerializedName("id")
    val id: String
) : AbstractTypeViewModel(ARTICLE_TYPE), Parcelable
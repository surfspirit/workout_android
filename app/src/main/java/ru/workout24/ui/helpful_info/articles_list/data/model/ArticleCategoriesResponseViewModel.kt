package ru.workout24.ui.helpful_info.articles_list.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArticleCategoriesResponseViewModel(
    @SerializedName("categories")
    val categories: List<ArticleCategoryViewModel>
) :  Parcelable
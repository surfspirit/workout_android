package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleCategoryViewModel
import io.reactivex.Single


@Dao
interface DaoCategories {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<ArticleCategoryViewModel>)


    @Query("""SELECT * from ArticleCategoryViewModel """)
    fun getAll(): Single<List<ArticleCategoryViewModel>>
}
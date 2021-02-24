package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.workout24.ui.statistics_layer.statistics.dto.UserStatsDate


@Dao
interface DaoUserStatisticsDates {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<UserStatsDate?>)


    @Query("""SELECT * from UserStatsDate ORDER BY date DESC""")
    fun getAll(): Single<List<UserStatsDate>>

    @Query("DELETE from UserStatsDate")
    fun clearAll(): Completable
}
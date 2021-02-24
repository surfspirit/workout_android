package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.network.UserStatistic
import io.reactivex.Single


@Dao
interface DaoUserStatistics {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<UserStatistic?>)


    @Query("""SELECT * from UserStatistic where createdDay = :date ORDER BY createdAt DESC """)
    fun getAll(date:String): Single<List<UserStatistic>>

    @Query("DELETE from UserStatistic")
    fun clearAll()
}
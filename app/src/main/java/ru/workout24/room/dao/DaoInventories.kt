package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.once_exercise.Inventories
import io.reactivex.Flowable


@Dao
interface DaoInventories {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<Inventories?>)


    @Query("""SELECT * from Inventories """)
    fun getAll(): Flowable<List<Inventories>>
}
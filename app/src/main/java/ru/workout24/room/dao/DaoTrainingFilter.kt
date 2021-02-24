package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTrainingFilter
import io.reactivex.Flowable

@Dao
interface DaoTrainingFilter {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<OnceTrainingFilter?>)


    @Query("""SELECT * from OnceTrainingFilter """)
    fun getAll(): Flowable<List<OnceTrainingFilter>>
}
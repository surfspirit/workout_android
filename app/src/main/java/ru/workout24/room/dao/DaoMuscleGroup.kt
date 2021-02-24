package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import io.reactivex.Flowable


@Dao
interface DaoMuscleGroup {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<MuscleGroup?>)


    @Query("""SELECT * from MuscleGroup """)
    fun getAll(): Flowable<List<MuscleGroup>>
}
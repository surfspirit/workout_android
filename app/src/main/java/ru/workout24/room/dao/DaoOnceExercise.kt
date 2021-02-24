package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.once_exercise.OnceExercise
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface DaoOnceExercise {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<OnceExercise?>)


    @Query("""SELECT * from OnceExercise """)
    fun getAll(): Flowable<List<OnceExercise>>

    @Query("""SELECT * from OnceExercise """)
    fun getAllSingle(): Single<List<OnceExercise>>
}
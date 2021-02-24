package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgramFilter
import io.reactivex.Flowable

@Dao
interface DaoTrainingProgramFilter {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<TrainingProgramFilter?>)


    @Query("""SELECT * from TrainingProgramFilter """)
    fun getAll(): Flowable<List<TrainingProgramFilter>>
}
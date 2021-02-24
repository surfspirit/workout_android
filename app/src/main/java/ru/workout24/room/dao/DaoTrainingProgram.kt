package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgram
import io.reactivex.Flowable

@Dao
interface DaoTrainingProgram {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TrainingProgram?)

    @Query("""SELECT * from TrainingProgram  Where id=:id""")
    fun getById(id: String?): Flowable<TrainingProgram>

}
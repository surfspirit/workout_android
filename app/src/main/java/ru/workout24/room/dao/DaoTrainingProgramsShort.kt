package ru.workout24.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgramShortDescription
import io.reactivex.Flowable

@Dao
interface DaoTrainingProgramsShort {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<TrainingProgramShortDescription?>)


    @Query("""SELECT * from TrainingProgramShortDescription """)
    fun getAll(): Flowable<List<TrainingProgramShortDescription>>

    @Query("""SELECT * from TrainingProgramShortDescription """)
    fun getAllAsLiveData(): LiveData<List<TrainingProgramShortDescription>>
}
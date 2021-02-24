package ru.workout24.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.workout24.ui.trainings.training_programs.pojos.Training

@Dao
interface DaoTrainingsInProgram {
    @Query("""SELECT * from Training  Where id=:id""")
    fun getById(id: String?): LiveData<Training>
}
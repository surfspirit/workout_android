package ru.workout24.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.training_programs.pojos.Training
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface DaoTrainings {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Training?)

    //TODO: брать метод
    @Query("""SELECT * from Training  Where id=:id""")
    fun getById(id: String?): Flowable<Training>

    @Query("""SELECT * from Training  Where id=:id""")
    fun getByIdSingle(id: String?): Single<Training>
}
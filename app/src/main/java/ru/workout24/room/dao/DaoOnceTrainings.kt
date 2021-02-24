package ru.workout24.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTraining
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface DaoOnceTrainings {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<OnceTraining?>)

    //TODO:убрать
    @Query("""SELECT * from OnceTraining """)
    fun getAll(): Flowable<List<OnceTraining>>

    @Query("""SELECT * from OnceTraining """)
    fun getAllSingle(): Single<List<OnceTraining>>

    @Query("""SELECT * from OnceTraining """)
    fun getAllAsLiveData(): LiveData<List<OnceTraining>>

}
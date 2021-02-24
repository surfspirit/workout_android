package ru.workout24.ui.workout_diary.data.dto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WorkoutDiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleTrainings(item: List<TrainingDto>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleExercises(item: List<SingleExerciseDto>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrainingSets(item: List<TrainingSetDto>?)

    @Query("""SELECT * from DiaryTraining """)
    fun getAllSingleTrainings(): Single<List<TrainingDto>>

    @Query("""SELECT * from DiarySingleExercise""")
    fun getAllSingleExercises(): Single<List<SingleExerciseDto>>

    @Query("""SELECT * from DiaryTrainingSet""")
    fun getAllTrainingSets(): Single<List<TrainingSetDto>>

    @Query("""SELECT * from DiaryTraining  Where month=:month AND year=:year""")
    fun getSingleTrainingsByMonthAndYear(year: Int, month: Int): Single<List<TrainingDto>>

    @Query("""SELECT * from DiarySingleExercise  Where month=:month AND year=:year""")
    fun getSingleExercisesByMonthAndYear(year: Int, month: Int): Single<List<SingleExerciseDto>>

    @Query("""SELECT * from DiaryTrainingSet  Where month=:month AND year=:year""")
    fun getTrainingSetsByMonthAndYear(year: Int, month: Int): Single<List<TrainingSetDto>>

    @Query("""DELETE from DiaryTraining  Where month=:month AND year=:year""")
    fun deleteSingleTrainingsByMonthAndYear(year: Int, month: Int): Completable

    @Query("""DELETE from DiarySingleExercise  Where month=:month AND year=:year""")
    fun deleteSingleExercisesByMonthAndYear(year: Int, month: Int): Completable

    @Query("""DELETE from DiaryTrainingSet  Where month=:month AND year=:year""")
    fun deleteTrainingSetsByMonthAndYear(year: Int, month: Int): Completable
}
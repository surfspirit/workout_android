package ru.workout24.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.workout24.network.User
import ru.workout24.network.UserStatistic
import ru.workout24.room.dao.*
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleCategoryViewModel
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleViewModel
import ru.workout24.ui.statistics_layer.statistics.dto.UserStatsDate
import ru.workout24.ui.statistics_layer.statistics_select_trainings.data.dto.DaoFilters
import ru.workout24.ui.statistics_layer.statistics_select_trainings.data.dto.FilterDto
import ru.workout24.ui.trainings.once_exercise.Inventories
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import ru.workout24.ui.trainings.once_exercise.OnceExercise
import ru.workout24.ui.trainings.once_trainings.pojos.OnceSet
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTraining
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTrainingFilter
import ru.workout24.ui.trainings.training_programs.pojos.*
import ru.workout24.ui.trainings.training_programs.pojos.Set
import ru.workout24.ui.workout_diary.data.dto.SingleExerciseDto
import ru.workout24.ui.workout_diary.data.dto.TrainingDto
import ru.workout24.ui.workout_diary.data.dto.TrainingSetDto
import ru.workout24.ui.workout_diary.data.dto.WorkoutDiaryDao


@Database(
    version = AppDatabase.VERSION, entities = [
        Post::class,
        Comment::class,
        TrainingProgramShortDescription::class,
        Training::class,
        TrainingProgram::class,
        Week::class,
        OnceExercise::class,
        TrainingProgramFilter::class,
        OnceTrainingFilter::class,
        OnceTraining::class,
        ExerciseTrainingProgram::class,
        Set::class,
        OnceSet::class,
        MuscleGroup::class,
        Inventories::class,
        User::class,
        UserStatistic::class,
        FilterDto::class,
        SingleExerciseDto::class,
        TrainingDto::class,
        TrainingSetDto::class,
        ArticleViewModel::class,
        ArticleCategoryViewModel::class,
        UserStatsDate::class
    ], exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val exampleDao: DaoExample
    abstract val daoTrainingProgramsShort: DaoTrainingProgramsShort
    abstract val daoOnceTrainings: DaoOnceTrainings
    abstract val daoTrainingProgram: DaoTrainingProgram
    abstract val daoOnceExercise: DaoOnceExercise
    abstract val daoTrainingProgramFilter: DaoTrainingProgramFilter
    abstract val daoTrainingFilter: DaoTrainingFilter
    abstract val daoTrainingsInProgram: DaoTrainingsInProgram
    abstract val daoTrainings: DaoTrainings
    abstract val daoInventories: DaoInventories
    abstract val daoMuscleGroup: DaoMuscleGroup
    abstract val daoUser: DaoUser
    abstract val daoUserStatistics: DaoUserStatistics
    abstract val daoFilters: DaoFilters
    abstract val daoWorkoutDiary: WorkoutDiaryDao
    abstract val daoCategories: DaoCategories
    abstract val daoUserStatsDates: DaoUserStatisticsDates

    companion object {
        const val VERSION = 7

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `Filter` (`id` INTEGER, `name` TEXT, " +
                        "PRIMARY KEY(`id`))")
            }
        }
    }
}

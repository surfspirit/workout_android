package ru.workout24.dagger.modules

import dagger.Provides
import javax.inject.Singleton
import android.app.Application
import androidx.room.Room
import ru.workout24.room.AppDatabase
import ru.workout24.room.dao.*
import dagger.Module


@Module
class RoomModule(mApplication: Application) {
    var demoDatabase: AppDatabase =
        Room.databaseBuilder(mApplication, AppDatabase::class.java, "demo-db")
//            .addMigrations(AppDatabase.MIGRATION_2_3)
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    internal fun providesRoomDatabase(): AppDatabase {
        return demoDatabase
    }


    @Singleton
    @Provides
    internal fun providesDaoTrainingProgramsShort(demoDatabase: AppDatabase): DaoTrainingProgramsShort {
        return demoDatabase.daoTrainingProgramsShort
    }

    @Singleton
    @Provides
    internal fun providesDaoTrainings(demoDatabase: AppDatabase): DaoOnceTrainings {
        return demoDatabase.daoOnceTrainings
    }

    @Singleton
    @Provides
    internal fun providesDaoTrainingProgram(demoDatabase: AppDatabase): DaoTrainingProgram {
        return demoDatabase.daoTrainingProgram
    }

    @Singleton
    @Provides
    internal fun providesDaoOnceExercise(demoDatabase: AppDatabase): DaoOnceExercise {
        return demoDatabase.daoOnceExercise
    }

    @Singleton
    @Provides
    internal fun providesDaoTrainingProgramFilter(demoDatabase: AppDatabase): DaoTrainingProgramFilter {
        return demoDatabase.daoTrainingProgramFilter
    }

    @Singleton
    @Provides
    internal fun providesDaoTrainingFilter(demoDatabase: AppDatabase): DaoTrainingFilter {
        return demoDatabase.daoTrainingFilter
    }

    @Singleton
    @Provides
    internal fun providesDaoTrainingsInProgram(demoDatabase: AppDatabase): DaoTrainingsInProgram {
        return demoDatabase.daoTrainingsInProgram
    }

    @Singleton
    @Provides
    internal fun providesDaoTrainingsbyid(demoDatabase: AppDatabase): DaoTrainings {
        return demoDatabase.daoTrainings
    }

    @Singleton
    @Provides
    internal fun providesDaoInventories(demoDatabase: AppDatabase): DaoInventories {
        return demoDatabase.daoInventories
    }

    @Singleton
    @Provides
    internal fun providesDaoMuscleGroup(demoDatabase: AppDatabase): DaoMuscleGroup {
        return demoDatabase.daoMuscleGroup
    }

    @Singleton
    @Provides
    internal fun providesDaoUser(demoDatabase: AppDatabase): DaoUser {
        return demoDatabase.daoUser
    }
}
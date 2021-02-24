package ru.workout24.dagger.modules

import ru.workout24.room.repositories.*
import ru.workout24.ui.lk_layer.edit_profile.repository.UserRepository
import dagger.Module

@Module
abstract class RepositoryModule {

    //abstract fun injectRep(rep: RepositoryExample)
//    abstract fun injectRep(rep: TrainingProgramShortRepository)
    abstract fun injectRep(rep: TrainingProgramRepository)
    abstract fun injectRep(rep: OnceExerciseRepository)
    abstract fun injectRep(rep: TrainingProgramFilterRepository)
    abstract fun injectRep(rep: OnceTrainingsRepository)
    abstract fun injectRep(rep: TrainingFilterRepository)
    abstract fun injectRep(rep: TrainingByIdRepository)
    abstract fun injectRep(rep: UserRepository)
}
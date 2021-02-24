package ru.workout24.dagger.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.workout24.dagger.ViewModelKey
import ru.workout24.ui.RootViewModel
import ru.workout24.ui.auth_layer.anket_layer.AnketViewModel
import ru.workout24.ui.auth_layer.test_layer.VMFitnessTest
import ru.workout24.ui.sub_layer.subscription.VMChooseSubscription
import ru.workout24.ui.auth_layer.register_login.VMForgotPassword
import ru.workout24.ui.auth_layer.register_login.VMRegisterLogin
import ru.workout24.ui.lk_layer.edit_profile.VMEditProfile
import ru.workout24.ui.sub_layer.subscription.VMGlobalChooseSubscription
import ru.workout24.ui.trainings.once_exercise.VMOnceExercisesGlobal
import ru.workout24.ui.trainings.once_trainings.VMTrainings
import ru.workout24.ui.trainings.training_programs.VMTrainingsShort
import ru.workout24.ui.trainings.training_programs.program.VMTrainingProgram
import ru.workout24.ui.trainings.training_programs.training.VMAboutTraining
import ru.workout24.utills.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(VMGlobalChooseSubscription::class)
    abstract fun bindVMGlobalChooseSubscription(viewModel: VMGlobalChooseSubscription): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMChooseSubscription::class)
    abstract fun bindVMChooseSubscription(viewModel: VMChooseSubscription): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMForgotPassword::class)
    abstract fun bindVMForgotPassword(viewModel: VMForgotPassword): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMRegisterLogin::class)
    abstract fun bindVMRegisterLogin(viewModel: VMRegisterLogin): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMFitnessTest::class)
    abstract fun bindVMFitnessTest(viewModel: VMFitnessTest): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RootViewModel::class)
    abstract fun bindMyTicketViewModel(viewModel: RootViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AnketViewModel::class)
    abstract fun bindAnketViewModel(viewModel: AnketViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMTrainingsShort::class)
    abstract fun bindVMTrainingsShort(viewModel: VMTrainingsShort): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMTrainings::class)
    abstract fun bindVMTrainings(viewModel: VMTrainings): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMTrainingProgram::class)
    abstract fun bindVMTrainingProgram(viewModel: VMTrainingProgram): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMOnceExercisesGlobal::class)
    abstract fun bindVMOnceExercises(viewModel: VMOnceExercisesGlobal): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMAboutTraining::class)
    abstract fun bindVMAboutTraining(viewModel: VMAboutTraining): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VMEditProfile::class)
    abstract fun bindVMEditProfile(viewModel: VMEditProfile): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(VMWorkoutDiary::class)
//    abstract fun bindVMWorkoutDiary(viewModel: VMWorkoutDiary): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory):
            ViewModelProvider.Factory
}
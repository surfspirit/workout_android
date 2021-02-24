package ru.workout24.ui.statistics_layer.once_exercises_trainings.data

import ru.workout24.ui.trainings.once_exercise.OnceExercise
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

const val ONCE_EXERCISES_TYPE = 11

class OnceExercisesViewModel(val item: OnceExercise): AbstractTypeViewModel(ONCE_EXERCISES_TYPE)
package ru.workout24.ui.statistics_layer.once_exercises_trainings.mapper

import androidx.arch.core.util.Function
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceExercisesViewModel
import ru.workout24.ui.trainings.once_exercise.OnceExercise
import ru.workout24.utills.toArrayList

class OnceExercisesMapper: Function<List<OnceExercise>?, ArrayList<OnceExercisesViewModel>> {
    override fun apply(input: List<OnceExercise>?): ArrayList<OnceExercisesViewModel> {
        return input?.map { OnceExercisesViewModel(it) }?.toArrayList() ?: ArrayList(0)
    }
}
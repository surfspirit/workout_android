package ru.workout24.ui.statistics_layer.once_exercises_trainings.mapper

import androidx.arch.core.util.Function
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceTrainingsViewModel
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTraining
import ru.workout24.utills.toArrayList

class OnceTrainingMapper: Function<List<OnceTraining>?, ArrayList<OnceTrainingsViewModel>> {
    override fun apply(input: List<OnceTraining>?): ArrayList<OnceTrainingsViewModel> {
        return input?.map { OnceTrainingsViewModel(it) }?.toArrayList() ?: ArrayList(0)
    }
}
package ru.workout24.ui.statistics_layer.once_exercises_trainings.data

import ru.workout24.ui.trainings.once_trainings.pojos.OnceTraining
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

const val ONCE_TRAININGS_TYPE = 12

class OnceTrainingsViewModel(val item: OnceTraining): AbstractTypeViewModel(ONCE_TRAININGS_TYPE)
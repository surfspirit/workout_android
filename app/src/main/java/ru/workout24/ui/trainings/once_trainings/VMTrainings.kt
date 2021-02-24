package ru.workout24.ui.trainings.once_trainings

import androidx.lifecycle.Transformations
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.statistics_layer.once_exercises_trainings.mapper.OnceTrainingMapper
import ru.workout24.utills.base.BaseViewModel
import javax.inject.Inject

class VMTrainings @Inject constructor(
    val resourceProvider: ResourceProvider
) : BaseViewModel() {
    private val trainingsRes = resourceProvider.trainings

    val trainings = Transformations.map(trainingsRes.liveData, OnceTrainingMapper())

    fun loadTrainings(filter: HashMap<String, String>? = null) {
        trainingsRes.query = filter
        trainingsRes.load()
    }
}

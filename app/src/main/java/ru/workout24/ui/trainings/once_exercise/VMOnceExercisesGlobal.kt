package ru.workout24.ui.trainings.once_exercise


import ru.workout24.utills.base.BaseViewModel
import ru.workout24.network.*
import javax.inject.Inject

class VMOnceExercisesGlobal @Inject constructor(
    val resourceProvider: ResourceProvider
) : BaseViewModel() {
    var filters: HashMap<String, String>? = null
    private val exercisesResource = resourceProvider.onceExercises
    private val user = resourceProvider.userResource
    val liveData = exercisesResource.liveData

    fun getExercises() {
        exercisesResource.query = filters
        exercisesResource.load()
    }
}

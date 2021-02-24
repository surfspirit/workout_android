package ru.workout24.ui.trainings.training_programs

import ru.workout24.utills.base.BaseViewModel
import ru.workout24.network.ResourceProvider
import java.util.HashMap
import javax.inject.Inject

class VMTrainingsShort @Inject constructor(
    private val provider: ResourceProvider
) : BaseViewModel() {
    private val programsRes = provider.programShortDescription
    val programs = programsRes.liveData

    init {
        loadPrograms()
    }

    fun loadPrograms(filterItems: HashMap<String, String>? = null) {
        programsRes.query = filterItems
        programsRes.load()
    }
}

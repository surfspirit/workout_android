package ru.workout24.ui.statistics_layer.once_exercises_trainings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.room.AppDatabase
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelWithFiltersFactory(
    private val api: Api,
    private val db: AppDatabase,
    private val res: ResourceProvider,
    private val type: SelectOnceExerciseOrTrainingsFragment.Companion.OpenType,
    private val filters: HashMap<String, String>?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VMSelectOnceExercises(api, db, res, type, filters) as T
    }
}


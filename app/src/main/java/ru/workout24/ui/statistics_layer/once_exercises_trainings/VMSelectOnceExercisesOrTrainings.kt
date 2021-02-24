package ru.workout24.ui.statistics_layer.once_exercises_trainings

import androidx.lifecycle.*
import ru.workout24.network.*
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.edit_statistics_entry.TargetType
import ru.workout24.ui.statistics_layer.edit_statistics_entry.TrainingTarget
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceExercisesViewModel
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceTrainingsViewModel
import ru.workout24.ui.statistics_layer.once_exercises_trainings.mapper.OnceExercisesMapper
import ru.workout24.ui.statistics_layer.once_exercises_trainings.mapper.OnceTrainingMapper
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import javax.inject.Inject

class VMSelectOnceExercises @Inject constructor(
    private val api: Api,
    private val db: AppDatabase,
    private val res: ResourceProvider,
    private val type: SelectOnceExerciseOrTrainingsFragment.Companion.OpenType,
    private val filtersList: HashMap<String, String>?
) : BaseViewModel() {
    private val exercisesRes = res.onceExercises
    private val trainingsRes = res.trainings
    private val resource = when (type) {
        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_EXERCISES -> exercisesRes
        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_TRAININGS -> trainingsRes
    }
    private val resourceLiveData = when (type) {
        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_EXERCISES -> Transformations.map(
            exercisesRes.liveData,
            OnceExercisesMapper()
        )
        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_TRAININGS -> Transformations.map(
            trainingsRes.liveData,
            OnceTrainingMapper()
        )
    }
    private val resourceUser = res.userResource
    val onceExercises = resourceLiveData
    val errors = resource.errorLiveData

    val selectedItem = MutableLiveData<AbstractTypeViewModel?>()

    fun load() {
        resource.query = filtersList
        resource.load()
    }

    fun done() {
        selectedItem.value?.let {
            when (type) {
                SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_EXERCISES -> {
                    val item = (it as OnceExercisesViewModel).item
                    TrainingTarget.setTarget(
                        item.id,
                        TargetType.SINGLE_EXERCISE,
                        item.name!!
                    )
                }
                SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_TRAININGS -> {
                    val item = (it as OnceTrainingsViewModel).item
                    TrainingTarget.setTarget(
                        item.id,
                        TargetType.TRAINING,
                        item.name!!
                    )
                }
            }
        }
    }
}

class CombinedTwoLiveData<T, K, S>(
    source1: LiveData<T>,
    source2: LiveData<K>,
    private val combine: (data1: T?, data2: K?) -> S
) : MediatorLiveData<S>() {

    private var data1: T? = null
    private var data2: K? = null

    init {
        super.addSource(source1) {
            data1 = it
            value = combine(data1, data2)
        }
        super.addSource(source2) {
            data2 = it
            value = combine(data1, data2)
        }
    }

    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}

class CombinedThreeLiveData<T1, T2, T3, S>(
    source1: LiveData<T1>,
    source2: LiveData<T2>,
    source3: LiveData<T3>,
    private val combine: (data1: T1?, data2: T2?, data3: T3?) -> S
) : MediatorLiveData<S>() {

    private var data1: T1? = null
    private var data2: T2? = null
    private var data3: T3? = null

    init {
        super.addSource(source1) {
            data1 = it
            value = combine(data1, data2, data3)
        }
        super.addSource(source2) {
            data2 = it
            value = combine(data1, data2, data3)
        }
        super.addSource(source3) {
            data3 = it
            value = combine(data1, data2, data3)
        }
    }

    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}
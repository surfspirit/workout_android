package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.statistics_layer.once_exercises_trainings.CombinedThreeLiveData
import ru.workout24.utills.RxSingleLiveData
import ru.workout24.utills.base.BaseViewModel
import javax.inject.Singleton

class VMDiaryDoneTraining(
    private val api: Api,
    private val resourceProvider: ResourceProvider,
    private val trainingId: String,
    private val assigmentId: String
) : BaseViewModel() {
    private val trainingResource by lazy {
        val value = resourceProvider.onceTraining
        value.query = trainingId
        value
    }
    private val trainingResultResource by lazy {
        val value = resourceProvider.onceTrainingResult
        value.query = assigmentId
        value
    }
    private val trainingMetaResultResource by lazy {
        val value = resourceProvider.onceTrainingResultMeta
        value.query = assigmentId
        value
    }
    val trainingLiveData = CombinedThreeLiveData(
        trainingResource.liveData,
        trainingResultResource.liveData,
        trainingMetaResultResource.liveData
    ) { t1, t2, t3 ->
        Triple(t1, t2, t3)
    }
    val startTrainingAssignedId by lazy {
        RxSingleLiveData<String>()
    }
    val startTrainingAssignedError by lazy {
        RxSingleLiveData<String>()
    }

    fun load() {
        trainingResource.loadIfNeeded()
        trainingResultResource.loadIfNeeded()
        trainingMetaResultResource.loadIfNeeded()
    }

    fun startTraining(trainingId: String) {
        compositeDisposable.add(
            api.startTrainingSingle(trainingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    if (data.isSuccessful && data.data != null) {
                        startTrainingAssignedId.postValue(data.data!!.trainingAssignmentId)
                    } else {
                        startTrainingAssignedError.postValue("Не удалось начать тренировку")
                    }
                }, {
                    startTrainingAssignedError.postValue("Не удалось начать тренировку")
                })
        )
    }
}

@Suppress("UNCHECKED_CAST")
@Singleton
class VMDiaryDoneTrainingFactory(
    private val api: Api,
    private val resourceProvider: ResourceProvider,
    private val trainingId: String,
    private val assignmentId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        VMDiaryDoneTraining(api, resourceProvider, trainingId, assignmentId) as T
}
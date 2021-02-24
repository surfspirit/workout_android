package ru.workout24.ui.exercises_execution

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observable.fromArray
import io.reactivex.Observable.fromIterable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.runOnUiThread
import ru.workout24.features.AppLifecycleObserver
import ru.workout24.features.CustomSoundsPlayer
import ru.workout24.network.*
import ru.workout24.ui.auth_layer.test_layer.pojos.TestExerciseResultDto
import ru.workout24.ui.exercises_execution.data.dto.ExerciseResultDto
import ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto.StatisticsEntryResponseDto
import ru.workout24.ui.trainings.training_programs.pojos.Set
import ru.workout24.ui.trainings.training_programs.pojos.SetWithExercisesQueue
import ru.workout24.utills.DIFFICULTY
import ru.workout24.utills.TARGET_TYPE
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.view.Photo
import java.util.*
import kotlin.collections.ArrayList

internal class ExercisesExecutionViewModel(
    private val appContext: Context,
    private val api: Api,
    private val openType: ExercisesExecutionHostFragment.OPEN_TYPE,
    private val setList: ArrayList<Set>,
    private val assignmentId: String
) : BaseViewModel() {
    private val setQueue = ArrayDeque<SetWithExercisesQueue>(0)

    // результаты упражнений
    private val exerciseResults = ArrayList<ExerciseResultDto>(0)

    val currentSet get() = setQueue.first

    val currentSetCount get() = setQueue.firstOrNull()?.exercisesCount
    val setsCount get() = setQueue.size

    val currentExercise get() = currentSet.currentExercise
    val currentSetExercisesCount get() = currentSet.exercisesCount

    val isShowProgressView get() = currentSet.allExercises != 1
    private val isNextExercisePossible get() = (currentSetCount != null && currentSetCount != 0) || setsCount != 0

    val showProgressDialog = MutableLiveData<Boolean>(false)
    val showApiError = MutableLiveData<Boolean>(false)

    // region Timer declaration
    val processTimerData = MutableLiveData("00:00")
    val pauseTimerData = MutableLiveData<String>()
    val startedTimerState = MutableLiveData<TimerState>()
    val isPauseTimerVisible = MutableLiveData(false)

    private val sound = CustomSoundsPlayer(appContext).apply { isLooping = false }
    private val isAppActiveObservable = AppLifecycleObserver.isActive.subscribe { isAppActive ->
        if (!isAppActive) {
            processTimer.stop()
            pauseTimer.stop()
        } else {
            if (isPauseTimerVisible.value == true) {
                pauseTimer.start()
            } else {
                processTimer.start()
            }
        }
    }

    private val processTimer = ProcessTimer().apply {
        setListener(object : ProcessTimer.TimerListener {
            override fun onTimerStart() {
                startedTimerState.postValue(TimerState.PROCESS)
                sound.playSound(CustomSoundsPlayer.Companion.SOUNDS.EXERCISE_START)
            }

            override fun onTimerStop() {
                sound.stopSound()
            }

            override fun onTickTime(time: String, seconds: Long) {
                if (seconds >= 1L) {
                    sound.playSound(CustomSoundsPlayer.Companion.SOUNDS.EXERCISES_PROCESS)
                }
                processTimerData.postValue(time)
                appContext.runOnUiThread {
                    val estimateTime =
                        currentExercise.exerciseTrainingProgram.estimateTime?.toLong() ?: 0
                    if (estimateTime == seconds) {
                        moveToResultPickerFragment()
                    }
                }
            }
        })
    }
    private val pauseTimer = ProcessTimer().apply {
        setListener(object : ProcessTimer.TimerListener {
            override fun onTimerStart() {
                startedTimerState.postValue(TimerState.PAUSE)
                isPauseTimerVisible.postValue(true)
            }

            override fun onTimerStop() {
                isPauseTimerVisible.postValue(false)
                sound.stopSound()
            }

            override fun onTickTime(time: String, seconds: Long) {
                if (seconds == 10L) {
                    sound.playSound(CustomSoundsPlayer.Companion.SOUNDS.RECREATION_END)
                }
                pauseTimerData.postValue(time)
                appContext.runOnUiThread {
                    val pauseTime = currentExercise.exerciseTrainingProgram.pauseTime?.toLong() ?: 0
                    if (seconds == 0L) {
                        moveToDescriptionFragment()
                    }
                }
            }
        })
    }
    // endregion

    init {
        setList
        setQueue.addAll(setList.mapIndexed { index, set -> set.toSetWithExercisesQueue(index) })
    }

    fun startProcessTimer() {
        if (!processTimer.isStarted && isNextExercisePossible) {
            processTimer.clear()
            processTimer.start()
        }
    }

    fun startPauseTimer() {
        if (!pauseTimer.isStarted) {
            pauseTimer.clear()
            pauseTimer.seconds = currentExercise.exerciseTrainingProgram.pauseTime?.toLong() ?: 0
            pauseTimer.startReverse()
        }
    }

    private fun nextExercise() {
        if (currentSetCount == 0) {
            if (setsCount == 0) {
                finish()
            } else {
                setQueue.removeFirst()
                if (!isNextExercisePossible) {
                    finish()
                }
            }
        } else {
            currentSet.next()
            if (currentSet.isEmpty) {
                setQueue.removeFirst()
                if (!isNextExercisePossible) {
                    finish()
                }
            }
        }
    }

    private fun finish() {
        processTimer.apply {
            stop()
            clear()
            removeListener()
        }
        pauseTimer.apply {
            stop()
            clear()
            removeListener()
        }

        showProgressDialog.postValue(true)
        when (openType) {
            ExercisesExecutionHostFragment.OPEN_TYPE.EXERCISE -> {
                showProgressDialog.postValue(false)
                moveToGlobalSaveResultsFragment()
            }
            ExercisesExecutionHostFragment.OPEN_TYPE.PROGRAM_TRAINING,
            ExercisesExecutionHostFragment.OPEN_TYPE.TRAINING -> {
                saveTrainingExerciseResults {
                    showProgressDialog.postValue(false)
                    moveToLikePickerFragment()
                }
            }
            ExercisesExecutionHostFragment.OPEN_TYPE.TEST -> {
                saveTestExercisesResult {
                    saveTestExercisesResultInStat(it) {
                        showProgressDialog.postValue(false)
                        moveToFinishTestFragment()
                    }
                }
            }
        }
    }

    // region api
    private var pendingExerciseRepeatCount = 1

    private var pendingTrainingDifficulty: DIFFICULTY = DIFFICULTY.VERY_SIMPLE
    private var pendingTrainingLike: Boolean = true
    private var pendingTrainingPhotos: List<Photo> = emptyList()
    private var pendingTrainingPhotosUrl: List<String> = emptyList()

    private fun putExercisesResultInContainer() {
        when (openType) {
            ExercisesExecutionHostFragment.OPEN_TYPE.EXERCISE -> {
                exerciseResults.add(
                    ExerciseResultDto.SingleExerciseDto(
                        id = currentExercise.exerciseTrainingProgram.id,
                        time = processTimer.seconds.toInt(),
                        count = pendingExerciseRepeatCount
                    )
                )
            }
            ExercisesExecutionHostFragment.OPEN_TYPE.PROGRAM_TRAINING,
            ExercisesExecutionHostFragment.OPEN_TYPE.TRAINING -> {
                exerciseResults.add(
                    ExerciseResultDto.TrainingExerciseDto(
                        id = currentExercise.exerciseTrainingProgram.id,
                        time = processTimer.seconds.toInt(),
                        count = pendingExerciseRepeatCount
                    )
                )
            }
            ExercisesExecutionHostFragment.OPEN_TYPE.TEST -> {
                exerciseResults.add(
                    ExerciseResultDto.TestExerciseDto(
                        id = currentExercise.exerciseTrainingProgram.id,
                        time = processTimer.seconds.toInt(),
                        count = pendingExerciseRepeatCount
                    )
                )
            }
        }
    }

    private fun saveSingleExercise(doAfterSaving: (value: BaseResponse<EndDto.SingleExerciseEndDto>) -> Unit) {
        compositeDisposable.add(
            api.postSingleExResult(exerciseResults.first() as ExerciseResultDto.SingleExerciseDto)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(doAfterSaving, ::apiErrorHandler)
        )
    }

    private fun saveSingleExerciseStat(
        data: BaseResponse<EndDto.SingleExerciseEndDto>,
        doAfterSaving: (StatisticsEntryResponseDto) -> Unit
    ) {
        postStatistic(
            TARGET_TYPE.SINGLE_EXERCISE_RESULTS,
            data.data?.id ?: "",
            pendingTrainingPhotosUrl
        ) { doAfterSaving(it) }
    }

    private fun saveTrainingExerciseResults(doAfterSaving: (ArrayList<EndDto.TrainingEndDto>) -> Unit) {
//        compositeDisposable.add(
//            Observable.fromIterable(exerciseResults).switchMap {
//                api.postTrainingExerciseResult(
//                    assignmentId,
//                    it as ExerciseResultDto.TrainingExerciseDto
//                )
//            }.observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(doAfterSaving, ::apiErrorHandler)
//        )
        val list = arrayListOf<EndDto.TrainingEndDto>()
        exerciseResults.forEach { exerciseResult ->
            compositeDisposable.add(
                api.postTrainingExerciseResult(
                    assignmentId,
                    exerciseResult as ExerciseResultDto.TrainingExerciseDto
                ).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        list.add(it)
                        exerciseResults.remove(exerciseResult)
                        if (exerciseResults.isEmpty()) {
                            doAfterSaving(list)
                        }
                    }, ::apiErrorHandler)
            )
        }
    }

    private fun saveTrainingExercisesResultStat(
        endResult: BaseResponse<EndDto.TrainingEndDto>,
        doAfterSaving: (StatisticsEntryResponseDto) -> Unit
    ) {
        postStatistic(TARGET_TYPE.TRAINING_RESULT, endResult.data?.id ?: "") {
            doAfterSaving(it)
        }
    }

    private fun saveTrainingResult(doAfterSaving: (value: BaseResponse<EndDto.TrainingEndDto>) -> Unit) {
        val trainingResult = EndTrainingBody(
            difficulty = pendingTrainingDifficulty.toString(),
            like = pendingTrainingLike
        )
        compositeDisposable.add(
            api.postTrainingResult(assignmentId, trainingResult)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(doAfterSaving, ::apiErrorHandler)
        )
    }

    private fun saveTestExercisesResult(doAfterSaving: (ArrayList<TestExerciseResultDto>) -> Unit) {
        val savedResults = arrayListOf<TestExerciseResultDto>()
        exerciseResults.forEach { exerciseResult ->
            compositeDisposable.add(
                api.postTestExerciseResult(
                    exerciseResult as ExerciseResultDto.TestExerciseDto
                ).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        it.data?.let { data -> savedResults += data }
                        exerciseResults.remove(exerciseResult)
                        if (exerciseResults.isEmpty()) {
                            doAfterSaving(savedResults)
                        }
                    }, ::apiErrorHandler)
            )
        }
    }

    private fun saveTestExercisesResultInStat(
        list: ArrayList<TestExerciseResultDto>,
        doAfterSaving: () -> Unit
    ) {
        list.forEachIndexed { index, item ->
            postStatistic(TARGET_TYPE.TEST_EXERCISE_RESULTS, item.id ?: "") {
                if (index == list.size - 1) {
                    doAfterSaving()
                }
            }
        }
    }

    fun saveCount(value: Int) {
        pendingExerciseRepeatCount = value
    }

    fun saveLike(value: Boolean) {
        pendingTrainingLike = value
    }

    fun saveHardness(value: DIFFICULTY) {
        pendingTrainingDifficulty = value
    }

    fun savePhotos(photos: List<Photo>) {
        pendingTrainingPhotos = photos
    }

    private fun savePhotosIsNeeded(doAfterSaving: () -> Unit) {
        if (pendingTrainingPhotos.isNotEmpty()) {
            compositeDisposable.add(
                Flowable.fromIterable(pendingTrainingPhotos).switchMap {
                    val requestFile = RequestBody.create(it.type, it.file)
                    val multipartBody =
                        MultipartBody.Part.createFormData("file", it.file.name, requestFile)
                    api.uploadFile(multipartBody, requestFile)
                }.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doFinally(doAfterSaving)
                    .subscribe({ uploadedFile ->
                        if (uploadedFile.isSuccessful && uploadedFile?.data?.url != null) pendingTrainingPhotosUrl += uploadedFile.data!!.url
                    }, ::apiErrorHandler)
            )
        } else {
            doAfterSaving()
        }
    }

    private fun postStatistic(
        type: TARGET_TYPE,
        id: String,
        images: List<String> = listOf(),
        doAfter: (StatisticsEntryResponseDto) -> Unit
    ) {
        compositeDisposable.add(
            api.addStatisticsEntrySingle(
                mapOf(
                    "target_type" to type.value,
                    "target_id" to id,
                    "photos_url" to images
                )
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    it.data?.let { statisticsEntry ->
                        doAfter(statisticsEntry)
                    } ?: apiErrorHandler(Throwable())
                }, ::apiErrorHandler)
        )
    }

    private fun apiErrorHandler(t: Throwable) {
        showApiError.postValue(true)
    }

    fun repeatExerciseSaving() {
        showApiError.postValue(false)
        finish()
    }

//    fun repeatTrainingSaving() {
//        showApiError.postValue(false)
//        saveTrainingResult {  }
//    }

//    private fun clearPendingValues() {
//        pendingExerciseRepeatCount = 0
//        pendingTrainingDifficulty = DIFFICULTY.NORMAL
//        pendingTrainingLike = true
//        pendingTrainingPhotos = emptyList()
//    }
    // endregion

    // region Navigation
    var exercisesExecutionRouteController: ExercisesExecutionHostRouteController? = null
    var exerciseTimerRouteController: ExerciseTimerHostRouteController? = null

    fun moveToExerciseTimerHostFragment() {
        exercisesExecutionRouteController?.moveToExerciseTimerHostFragment()
    }

    fun moveToDescriptionFragment() {
        pauseTimer.stop()
        putExercisesResultInContainer()
        nextExercise()
        if (isNextExercisePossible) {
            exerciseTimerRouteController?.moveToDescriptionFragment()
            startProcessTimer()
        }
    }

    fun moveToResultPickerFragment() {
        processTimer.stop()
        exerciseTimerRouteController?.moveToResultPickerFragment()
        startPauseTimer()
    }

    fun moveToLikePickerFragment() {
        exercisesExecutionRouteController?.moveToLikePickerFragment()
    }

    fun moveToHardnessPickerFragment() {
        exercisesExecutionRouteController?.moveToHardnessPickerFragment()
    }

    fun moveToSaveResultsFragment() {
        exercisesExecutionRouteController?.moveToSaveResultsFragment()
    }

    fun moveToGlobalSaveResultsFragment() {
        exercisesExecutionRouteController?.moveToGlobalSaveResultsFragment()
    }

    fun moveToFinishTestFragment() {
        exercisesExecutionRouteController?.moveToFinishTestFragment()
    }

    fun skipResultSaving() {
        if (openType == ExercisesExecutionHostFragment.OPEN_TYPE.EXERCISE) {
            saveSingleExercise {
                closeAll()
            }
        } else {
            saveTrainingResult { endResult ->
                // мб здесь статистику ненадо сохранять
                saveTrainingExercisesResultStat(endResult) { stat ->
                    closeAll()
                }
            }
        }
    }

    fun moveToEditStatisticFragment() {
        if (openType == ExercisesExecutionHostFragment.OPEN_TYPE.EXERCISE) {
            showProgressDialog.postValue(true)
            savePhotosIsNeeded {
                saveSingleExercise {
                    saveSingleExerciseStat(it) {
                        showProgressDialog.postValue(false)
                        exercisesExecutionRouteController?.moveToEditStatisticFragment(it)
                    }
                }
            }
        } else {
            saveTrainingResult { endResult ->
                showProgressDialog.postValue(true)
                savePhotosIsNeeded {
                    when (openType) {
                        ExercisesExecutionHostFragment.OPEN_TYPE.TEST -> {
                            postStatistic(
                                TARGET_TYPE.TEST_EXERCISE_RESULTS,
                                endResult.data?.id ?: "",
                                pendingTrainingPhotosUrl
                            ) {
                                exercisesExecutionRouteController?.moveToEditStatisticFragment(it)
                            }
                        }
                        ExercisesExecutionHostFragment.OPEN_TYPE.TRAINING,
                        ExercisesExecutionHostFragment.OPEN_TYPE.PROGRAM_TRAINING -> {
                            postStatistic(
                                TARGET_TYPE.TRAINING_RESULT,
                                endResult.data?.id ?: "",
                                pendingTrainingPhotosUrl
                            ) {
                                exercisesExecutionRouteController?.moveToEditStatisticFragment(it)
                            }
                        }
                    }
                }
            }
        }
    }

    fun closeAll() {
        stopTimers()
        exercisesExecutionRouteController?.closeAll()
    }
    // endregion

    fun stopTimers() {
        if (processTimer.isStarted) processTimer.stop()
        if (pauseTimer.isStarted) pauseTimer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        isAppActiveObservable.dispose()
    }
}

enum class TimerState {
    PROCESS, PAUSE
}

internal class ExercisesExecutionViewModelFactory(
    private val appContext: Context,
    private val api: Api,
    private val openType: ExercisesExecutionHostFragment.OPEN_TYPE,
    private val setList: ArrayList<Set>,
    private val assignmentId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExercisesExecutionViewModel(
            appContext,
            api,
            openType,
            setList,
            assignmentId
        ) as T
}
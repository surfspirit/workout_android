package ru.workout24.ui.workout_diary

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.workout24.features.CustomCalendar
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.workout_diary.data.dto.TrainingDto
import ru.workout24.ui.workout_diary.data.model.WorkoutViewModel.*
import ru.workout24.ui.workout_diary.mapper.WorkoutDiaryMapper
import ru.workout24.utills.RxSingleLiveData
import ru.workout24.utills.base.BaseViewModel
import javax.inject.Singleton

class VMWorkoutDiary(
    resourceProvider: ResourceProvider,
    private val api: Api
) : BaseViewModel() {
    private val calendarResource = resourceProvider.calendar
    private val calendar = CustomCalendar()
    private val mapper by lazy {
        WorkoutDiaryMapper(calendar)
    }
    val singleMessage by lazy {
        RxSingleLiveData<String>()
    }
    val calendarData by lazy {
        Transformations.map(calendarResource.liveData, mapper)
    }

    fun changeCalendarMode(mode: CalendarMode) {
        mapper.mode = mode
        calendar.discardWeekIndex()
        calendarResource.query = calendar.asCalendar()
        calendarResource.loadIfNeeded()
    }

    fun nextWeek() {
        calendar.nextWeek()
        changeCalendarCurrentWeek()
    }

    fun prevWeek() {
        calendar.prevWeek()
        changeCalendarCurrentWeek()
    }

    fun nextMonth() {
        calendar.nextMonth()
        changeCalendarMonthYear()
    }

    fun prevMonth() {
        calendar.prevMonth()
        changeCalendarMonthYear()
    }

    private fun changeCalendarCurrentWeek() {
        calendarResource.query = calendar.asCalendar()
        calendarResource.loadIfNeeded()
    }

    fun changeCalendarMonthYear() {
        calendarResource.clear()
        calendarResource.query = calendar.asCalendar()
        calendarResource.load()
    }

    fun moveLostProgramTraining(trainingViewModel: TrainingViewModel) {
        val training = trainingViewModel.value as TrainingDto
        training.programData!!.programId?.let {
            compositeDisposable.add(
                api.moveTrainingToTrainingSetEnd(training.assignmentId, it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        calendarResource.load()
                        singleMessage.postValue("Тренировка \"${training.trainingData?.name}\" успешно перенесена на следующую неделю")
                    }, {
                        singleMessage.postValue("Не удалось перенести тренировку \"${training.trainingData?.name}\"")
                    })
            )
        }
    }

    fun removeProgramTraining(trainingViewModel: TrainingViewModel) {
        val training = trainingViewModel.value as TrainingDto
        compositeDisposable.add(
            api.cancelTrainingSingle(training.assignmentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    calendarResource.load()
                    singleMessage.postValue("Тренировка \"${training.trainingData?.name}\" успешно удалена")
                }, {
                    singleMessage.postValue("Не удалось удалить тренировку \"${training.trainingData?.name}\"")
                })
        )
    }

    fun endProgram(item: ProgramHeaderViewModel) {
        compositeDisposable.add(
            api.endTrainingProgram(item.programId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    calendarResource.load()
                    singleMessage.postValue("Программа успешно завершена")
                }, {
                    singleMessage.postValue("Не удалось завершить программу")
                })
        )
    }
}

@Suppress("UNCHECKED_CAST")
@Singleton
class VMWorkoutDiaryFactory(
    private val resourceProvider: ResourceProvider,
    private val api: Api
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        VMWorkoutDiary(resourceProvider, api) as T
}
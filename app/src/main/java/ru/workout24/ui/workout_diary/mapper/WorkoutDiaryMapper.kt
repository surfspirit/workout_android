package ru.workout24.ui.workout_diary.mapper

import androidx.arch.core.util.Function
import org.jetbrains.anko.collections.forEachWithIndex
import ru.workout24.ui.workout_diary.CalendarMode
import ru.workout24.features.CustomCalendar
import ru.workout24.ui.workout_diary.adapter.HolderType
import ru.workout24.ui.workout_diary.data.dto.*
import ru.workout24.ui.workout_diary.data.model.*
import ru.workout24.ui.workout_diary.data.model.TrainingType
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import ru.workout24.utills.calendarFrom_ISO
import ru.workout24.utills.fromCalendarTo_dd_MMM_String
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

class WorkoutDiaryMapper(private val customCalendar: CustomCalendar) :
    Function<CalendarDto?, Pair<List<AbstractTypeViewModel>, Set<Calendar>>> {

    var mode: CalendarMode = CalendarMode.WEEK
    var calendar = customCalendar.asCalendar()

    override fun apply(input: CalendarDto?): Pair<List<AbstractTypeViewModel>, Set<Calendar>> =
        input?.let {
            val dtoList = input.createLinearListWithInput()
            return dtoList
                .sortAndMapLinearList()
                .createLinearListWithProgramHeaderAndCalendarDayViewModels()
                .pairingLinearListWithHighlightPoints(dtoList)
        } ?: run {
            return Pair(emptyList(), emptySet())
        }

    // region input to linear list
    /**
     * Добавляем пришедшие данные в один большой контейнер
     * Если тренировка программная, то для тренировки в поле programData сеттим объект ProgramData
     */
    private fun CalendarDto.createLinearListWithInput(): ArrayList<DateSortalable> {
        // контейнер для первичных(полученых с сервера или бд) дтошек
        return ArrayList<DateSortalable>(0).apply {
            // добавляем все тренировки, упражнения и тренировки из программы
            // в  один контейнер
            trainings.apply {
                addAll(filter { it.status != TrainingStatus.CANCELED })
            }
            singleExercises.apply {
                addAll(this)
            }
            trainingSets.apply {
                addAll(this)
            }
        }
    }

    /**
     * Сортируем dto и делаем из них линейный список
     */
    private fun List<DateSortalable>.sortAndMapLinearList(): List<WorkoutViewModel> {
        val days = customCalendar.getCurrentWeekDays()
        return filter { item ->
            when (item) {
                is TrainingSetDto -> {
                    item.filterTrainings.clear()
                    item.trainingsSortedByDate.forEach { programTraining ->
                        val itemDate = programTraining.startDate.calendarFrom_ISO()
                        when (mode) {
                            CalendarMode.WEEK -> {
                                if (days.contains(
                                        CustomCalendar.WeekDay(
                                            itemDate.get(Calendar.DAY_OF_MONTH),
                                            itemDate.get(Calendar.MONTH)
                                        )
                                    )
                                ) {
                                    item.filterTrainings.add(programTraining)
                                }
                            }
                            CalendarMode.MONTH -> {
                                if (calendar.get(Calendar.MONTH) == itemDate.get(Calendar.MONTH)) {
                                    item.filterTrainings.add(programTraining)
                                }
                            }
                        }
                    }
                    return@filter item.filterTrainings.isNotEmpty()
                }
                is TrainingDto, is SingleExerciseDto -> {
                    val itemDate = item.getDate()
                    when (mode) {
                        CalendarMode.WEEK -> {
                            return@filter days.contains(
                                CustomCalendar.WeekDay(
                                    itemDate.get(Calendar.DAY_OF_MONTH),
                                    itemDate.get(Calendar.MONTH)
                                )
                            )
                        }
                        CalendarMode.MONTH -> {
                            return@filter calendar.get(Calendar.MONTH) == itemDate.get(Calendar.MONTH)
                        }
                    }
                }
                else -> throw IllegalArgumentException("Тип ${item::class.simpleName} отсутствует в CalendarDto")
            }
            // сортируем по дате (см DateSortalable)
        }.sortedBy { it.getDate() }.map { item ->
            when (item) {
                is TrainingSetDto -> mapProgram(item)
                is TrainingDto -> mapTraining(HolderType.SINGLE_TRAINING, item)
                is SingleExerciseDto -> mapSingleExercise(item)
                else -> throw IllegalArgumentException("Тип ${item::class.simpleName} отсутствует в WorkoutViewModel")
            }
        }
    }

    /**
     * Маппим линейный список во вью модели
     */
    private fun List<WorkoutViewModel>.createLinearListWithProgramHeaderAndCalendarDayViewModels(): MutableList<AbstractTypeViewModel> {
        // контейнер для конечных вью-моделей для непосредственно отображения на экран
        return mutableListOf<AbstractTypeViewModel>().also { linearList ->
            forEachWithIndex { index, currentTrainingViewModel ->
                when (currentTrainingViewModel) {
                    is WorkoutViewModel.ProgramViewModel -> {
                        // добавляем карточку программы
                        linearList.add(
                            WorkoutViewModel.ProgramHeaderViewModel(
                                currentTrainingViewModel.name,
                                currentTrainingViewModel.duration.toString(),
                                currentTrainingViewModel.id
                            )
                        )
                        currentTrainingViewModel.trainings.forEachWithIndex { programIndex, currentProgramTrainingViewModel ->
                            val previousProgramTrainingViewModel =
                                currentTrainingViewModel.trainings.getOrNull(programIndex - 1)
                            linearList.addCalendarDayViewModelIfNeeded(
                                currentProgramTrainingViewModel,
                                previousProgramTrainingViewModel
                            )
                            linearList.add(currentProgramTrainingViewModel)
                        }
                    }
                    is WorkoutViewModel.TrainingViewModel -> {
                        val previousTrainingViewModel = getOrNull(index - 1)
                        // добавляем карточку с датой
                        linearList.addCalendarDayViewModelIfNeeded(
                            currentTrainingViewModel,
                            previousTrainingViewModel
                        )
                        linearList.add(currentTrainingViewModel)
                    }
                }
            }
        }
    }

    /**
     * Создаем [Pair] объект с замапленными данными и днями тренировок для календаря
     */
    private fun List<AbstractTypeViewModel>.pairingLinearListWithHighlightPoints(dtoList: List<DateSortalable>): Pair<List<AbstractTypeViewModel>, Set<Calendar>> {
        // набор хайлайтов для отображения в календаре
        val highlightPoints = mutableSetOf<Calendar>()
        dtoList.forEach {
            when(it){
                is TrainingSetDto -> highlightPoints.addAll(it.filterTrainings.map { it.getDate() })
                is TrainingDto, is SingleExerciseDto -> highlightPoints.add(it.getDate())
            }
        }
        return Pair(this, highlightPoints)
    }

    private fun MutableList<AbstractTypeViewModel>.addCalendarDayViewModelIfNeeded(
        currentTrainingViewModel: WorkoutViewModel,
        previousTrainingViewModel: WorkoutViewModel?
    ) {
        if (currentTrainingViewModel !is WorkoutViewModel.TrainingViewModel
            && previousTrainingViewModel !is WorkoutViewModel.TrainingViewModel
        ) return

        currentTrainingViewModel as WorkoutViewModel.TrainingViewModel
        val currentTrainingViewModelDate =
            (currentTrainingViewModel.value as DateSortalable).getDate()

        if (previousTrainingViewModel != null) {
            previousTrainingViewModel as WorkoutViewModel.TrainingViewModel
            val previousTrainingViewModelDate =
                (previousTrainingViewModel.value as DateSortalable).getDate()

            if (currentTrainingViewModelDate.get(Calendar.YEAR) != previousTrainingViewModelDate.get(
                    Calendar.YEAR
                )
                || currentTrainingViewModelDate.get(Calendar.MONTH) != previousTrainingViewModelDate.get(
                    Calendar.MONTH
                )
                || currentTrainingViewModelDate.get(Calendar.DAY_OF_MONTH) != previousTrainingViewModelDate.get(
                    Calendar.DAY_OF_MONTH
                )
            ) {
                add(WorkoutViewModel.CalendarDayViewModel(currentTrainingViewModelDate.fromCalendarTo_dd_MMM_String()!!))
            }
        } else {
            add(WorkoutViewModel.CalendarDayViewModel(currentTrainingViewModelDate.fromCalendarTo_dd_MMM_String()!!))
        }
    }
    // endregion

    // region map functions
    /**
     * Маппит дто в вью модель программы
     */
    private fun mapProgram(item: TrainingSetDto): WorkoutViewModel {
        return WorkoutViewModel.ProgramViewModel(
            id = item.id,
            name = item.name ?: "",
            duration = item.weeksCount ?: 0,
            startTime = item.startTime ?: "",
            trainings = (if (mode == CalendarMode.WEEK) item.filterTrainings else item.trainingsSortedByDate).map {
                mapTraining(HolderType.PROGRAM_TRAINING, it) as WorkoutViewModel.TrainingViewModel
            }
        )
    }

    /**
     * Маппит дто в вм одиночной тренировки или программной
     */
    private fun mapTraining(type: HolderType, item: TrainingDto): WorkoutViewModel {
        return WorkoutViewModel.TrainingViewModel(
            modelType = type,
            name = item.trainingData?.name ?: "",
            week = "",
            trainingType = getType(item.status),
            value = item
        )
    }

    /**
     * Маппит дто в вм одиночной упражнения
     */
    private fun mapSingleExercise(singleExercise: SingleExerciseDto): WorkoutViewModel {
        return WorkoutViewModel.TrainingViewModel(
            HolderType.SINGLE_EXERCISE,
            singleExercise.singleExerciseData?.name ?: "",
            "",
            TrainingType.DONE,
            singleExercise
        )
    }
    // endregion

    // region util functions
    /**
     * Получить тип отображения для тренировки
     */
    private fun getType(status: TrainingStatus?): TrainingType {
        return when (status) {
            TrainingStatus.STARTED, TrainingStatus.SCHEDULED -> TrainingType.UPCOMING
            TrainingStatus.CANCELED, TrainingStatus.SKIPPED -> TrainingType.LOST
            TrainingStatus.COMPLETED -> TrainingType.DONE
            else -> TrainingType.UPCOMING
        }
    }
    // endregion
}
package ru.workout24.ui.workout_diary.data.model

import android.os.Parcelable
import ru.workout24.ui.workout_diary.adapter.HolderType
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import kotlinx.android.parcel.Parcelize

sealed class WorkoutViewModel(type: HolderType): AbstractTypeViewModel(type.value) {
    @Parcelize
    class TrainingViewModel(
        val modelType: HolderType,
        val name: String,
        val week: String,
        val trainingType: TrainingType,
        val value: Parcelable
    ) : WorkoutViewModel(modelType), Parcelable

    /**
     * Не нужно использовать в адаптере, так как он только для маппинга
     */
    @Parcelize
    class ProgramViewModel(
        val id: String,
        val name: String,
        val duration: Int,
        val startTime: String,
        val trainings: List<TrainingViewModel>
    ) : WorkoutViewModel(HolderType.PROGRAM), Parcelable

    @Parcelize
    class ProgramHeaderViewModel(
        val programName: String,
        val duration: String,
        val programId: String,
        val isDone: Boolean = false
    ) : WorkoutViewModel(HolderType.PROGRAM_HEADER), Parcelable

    class CalendarDayViewModel(val day: String) : WorkoutViewModel(HolderType.CALENDAR_DAY)
}

enum class TrainingType {
    UPCOMING, DONE, LOST
}
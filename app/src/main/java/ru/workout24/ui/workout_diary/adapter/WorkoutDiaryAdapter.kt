package ru.workout24.ui.workout_diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.workout24.R
import ru.workout24.ui.workout_diary.adapter.holder.CalendarDayHolder
import ru.workout24.ui.workout_diary.adapter.holder.ProgramHeaderHolder
import ru.workout24.ui.workout_diary.adapter.holder.TrainingHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeAdapter
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

class WorkoutDiaryAdapter : AbstractTypeAdapter() {
    private var itemListener: OnItemListener? = null

    override fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> {
        return when (viewType) {
            HolderType.CALENDAR_DAY.value -> CalendarDayHolder(
                inflater.inflate(
                    R.layout.item_workout_diary_header,
                    parent,
                    false
                )
            )
            HolderType.PROGRAM_HEADER.value -> ProgramHeaderHolder(
                inflater.inflate(
                    R.layout.item_workout_diary_program,
                    parent,
                    false
                ),
                itemListener
            )
            HolderType.PROGRAM_TRAINING.value, HolderType.SINGLE_TRAINING.value, HolderType.SINGLE_EXERCISE.value -> TrainingHolder(
                inflater.inflate(
                    R.layout.item_workout_diary_traning,
                    parent,
                    false
                ),
                itemListener
            )
            else -> throw IllegalArgumentException("Нет такого типа")
        }
    }

    fun setOnOptionListener(listener: OnItemListener) {
        itemListener = listener
    }
}

interface OnItemListener {
    fun onItemClick(item: AbstractTypeViewModel)
    fun onOptionClick(view: View, item: AbstractTypeViewModel)
}

enum class HolderType(val value: Int) {
    CALENDAR_DAY(0),
    PROGRAM_HEADER(1),
    PROGRAM(2),
    PROGRAM_TRAINING(3),
    SINGLE_TRAINING(4),
    SINGLE_EXERCISE(5)
    /*, TRAINING_DONE(3), TRAINING_LOST(4)*/
}
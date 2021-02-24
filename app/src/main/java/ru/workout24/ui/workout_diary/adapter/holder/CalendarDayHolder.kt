package ru.workout24.ui.workout_diary.adapter.holder

import android.view.View
import android.widget.TextView
import ru.workout24.ui.workout_diary.data.model.WorkoutViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder

class CalendarDayHolder(val view: View): AbstractTypeHolder<WorkoutViewModel.CalendarDayViewModel>(view) {
    override fun bind(data: WorkoutViewModel.CalendarDayViewModel) {
        (view as TextView).text = data.day
    }
}
package ru.workout24.ui.workout_diary.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.workout_diary.adapter.OnItemListener
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.find
import ru.workout24.ui.workout_diary.data.model.WorkoutViewModel

class ProgramHeaderHolder(val view: View, val itemListener: OnItemListener?) :
    AbstractTypeHolder<WorkoutViewModel.ProgramHeaderViewModel>(view) {
    override fun bind(data: WorkoutViewModel.ProgramHeaderViewModel) {
        val title = view.find<TextView>(R.id.trainingTitle)
        val week = view.find<TextView>(R.id.trainingWeek)
        val option = view.find<ImageView>(R.id.options)
        title.text = data.programName
        week.text = "Продолжительность ${data.duration} недели"
        view.setOnClickListener { itemListener?.onItemClick(data) }
        option.setOnClickListener { view -> itemListener?.onOptionClick(view, data) }
    }
}
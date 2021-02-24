package ru.workout24.ui.workout_diary.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.workout24.R
import ru.workout24.ui.workout_diary.adapter.OnItemListener
import ru.workout24.ui.workout_diary.data.model.TrainingType
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.find
import ru.workout24.ui.workout_diary.data.model.WorkoutViewModel

class TrainingHolder(val view: View, val itemListener: OnItemListener?) : AbstractTypeHolder<WorkoutViewModel.TrainingViewModel>(view) {
    override fun bind(data: WorkoutViewModel.TrainingViewModel) {
        val ivCheck = view.find<ImageView>(R.id.check)
        val txtTrainingTitle = view.find<TextView>(R.id.trainingTitle)
        val txtTrainingWeek = view.find<TextView>(R.id.trainingWeek)
        val ivOptions = view.find<ImageView>(R.id.options)

        when (data.trainingType) {
            TrainingType.UPCOMING -> {

            }
            TrainingType.DONE -> {
                ivCheck.setColorFilter(ContextCompat.getColor(view.context, R.color.tomato))
            }
            TrainingType.LOST -> {
                val color = ContextCompat.getColor(view.context, R.color.whiteThree)
                ivCheck.setColorFilter(color)
//                ivCheck.visibility = View.INVISIBLE
                txtTrainingTitle.setTextColor(color)
                txtTrainingWeek.setTextColor(color)
            }
        }
        txtTrainingTitle.text = data.name
        txtTrainingWeek.text = data.week
        ivOptions.setOnClickListener { itemListener?.onOptionClick(it, data) }
        view.setOnClickListener { itemListener?.onItemClick(data) }
    }
}
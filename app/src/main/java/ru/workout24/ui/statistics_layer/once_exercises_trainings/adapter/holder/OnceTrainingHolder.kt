package ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter.holder

import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceTrainingsViewModel
import ru.workout24.utills.base.typed_holder_adapter.SelectListener
import ru.workout24.utills.base.typed_holder_adapter.SelectableTypeHolder
import ru.workout24.utills.goals
import ru.workout24.utills.hide
import ru.workout24.utills.levels
import ru.workout24.utills.show
import com.bumptech.glide.Glide
import org.jetbrains.anko.find

class OnceTrainingHolder(
    private val bindingView: View,
    private val listener: SelectListener
) : SelectableTypeHolder<OnceTrainingsViewModel>(listener, bindingView) {
    override fun bind(data: OnceTrainingsViewModel) {
        val txtName = bindingView.find<TextView>(R.id.txt_training_name)
        val txtGroups = bindingView.find<TextView>(R.id.txt_training_muscle_groups)
        val txtLevel = bindingView.find<TextView>(R.id.txt_training_level)
        val txtGoals = bindingView.find<TextView>(R.id.txt_training_goals)
        val ivLock = bindingView.find<FrameLayout>(R.id.iv_once_training_lock)
        val ivInv = bindingView.find<ImageView>(R.id.iv_once_training_inv)
        val ivImg = bindingView.find<ImageView>(R.id.iv_once_training_image)
        val ivCheck = bindingView.find<ImageView>(R.id.imageView12)
        if (isSelected) {
            ivCheck.show()
        } else {
            ivCheck.hide()
        }
        val item = data.item
        txtName.text = item.name
        txtGroups.text = TextUtils.join(", ", item.muscleGroups.map { it?.name })
        txtGoals.text = goals[item.goals]
        txtLevel.text = levels[item.trainingLevel]
        if (item.available == false) {
            ivLock.visibility = View.VISIBLE
        } else {
            ivLock.visibility = View.GONE
        }
        bindingView.setOnClickListener {
            listener.onSelect(adapterPosition, data)
        }
        Glide.with(bindingView.context).load(item.previewUrl).into(ivImg)
    }
}
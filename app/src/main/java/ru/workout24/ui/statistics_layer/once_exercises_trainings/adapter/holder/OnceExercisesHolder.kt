package ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.workout24.R
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceExercisesViewModel
import ru.workout24.utills.base.typed_holder_adapter.SelectListener
import ru.workout24.utills.base.typed_holder_adapter.SelectableTypeHolder
import ru.workout24.utills.hide
import ru.workout24.utills.show
import com.bumptech.glide.Glide
import org.jetbrains.anko.find

class OnceExercisesHolder(
    private val bindingView: View,
    private val listener: SelectListener
) :
    SelectableTypeHolder<OnceExercisesViewModel>(listener,bindingView) {
    override fun bind(data: OnceExercisesViewModel) {
        val txtName = bindingView.find<TextView>(R.id.txt_exercise_name)
        val ivImage = bindingView.find<ImageView>(R.id.iv_once_exercise_image)
        val lock = bindingView.find<ImageView>(R.id.iv_lock)
        val ivCheck = bindingView.find<ImageView>(R.id.iv_check)
        if (isSelected) {
            ivCheck.show()
        } else {
            ivCheck.hide()
        }
        txtName.text = data.item.name
        Glide.with(bindingView.context).load(data.item.previewUrl).into(ivImage)
        if (data.item.available == true) {
            lock.hide()
            bindingView.setOnClickListener {
                listener.onSelect(adapterPosition, data)
            }
        } else {
            lock.show()
            ivImage.setColorFilter(
                ContextCompat.getColor(
                    bindingView.context,
                    R.color.transparentBlack
                )
            )
        }
    }
}
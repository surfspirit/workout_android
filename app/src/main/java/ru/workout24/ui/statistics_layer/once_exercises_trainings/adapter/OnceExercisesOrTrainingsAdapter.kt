package ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import ru.workout24.R
import ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter.holder.OnceExercisesHolder
import ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter.holder.OnceTrainingHolder
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.ONCE_EXERCISES_TYPE
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.ONCE_TRAININGS_TYPE
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeAdapter
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

open class OnceExercisesAdapter(@LayoutRes headerRes: Int? = null, val showSelection: Boolean = true) :
    AbstractTypeAdapter(headerResId = headerRes) {
    private var itemSelectedCallback: (data: AbstractTypeViewModel?) -> Unit = {}

    override fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> = when (viewType) {
        ONCE_EXERCISES_TYPE -> OnceExercisesHolder(
            inflater.inflate(
                R.layout.item_select_once_exercise_item,
                parent,
                false
            ), this
        )
        ONCE_TRAININGS_TYPE -> OnceTrainingHolder(
            inflater.inflate(
                R.layout.item_select_once_training_item,
                parent,
                false
            ), this
        )
        else -> throw IllegalArgumentException()
    }

    fun setItemSelectedCallback(itemSelectedCallback: (pos: AbstractTypeViewModel?) -> Unit) {
        this.itemSelectedCallback = itemSelectedCallback
    }

    override fun onSelect(position: Int, data: AbstractTypeViewModel) {
        if (showSelection) super.onSelect(position, data)
        itemSelectedCallback(data)
    }
}
package ru.workout24.utills.base.typed_holder_adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractTypeHolder<ViewModel : AbstractTypeViewModel>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    @Suppress("UNCHECKED_CAST")
    fun adapterBind(data: AbstractTypeViewModel) {
        data as? ViewModel
            ?: throw IllegalArgumentException("Был передан не тот тип, который ожидался")
        bind(data)
    }

    abstract fun bind(data: ViewModel)
}

abstract class SelectableTypeHolder<ViewModel : AbstractTypeViewModel>(
    selectListener: SelectListener? = null,
    itemView: View
) : AbstractTypeHolder<ViewModel>(itemView) {
    var isSelected: Boolean = false
}
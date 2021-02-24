package ru.workout24.ui.trainings.pager.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Добавляет отступ снизу для [CategoryViewHolder]
 */
class CategoryOffsetDecorator(private val offsetPx: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildViewHolder(view) is CategoryViewHolder) outRect.bottom = offsetPx
    }
}
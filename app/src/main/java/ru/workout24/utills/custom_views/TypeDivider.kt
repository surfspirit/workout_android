package ru.workout24.utills.custom_views

import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class TypeDivider(
    private val holderType: Int,
    @Px private val height: Int,
    @ColorInt private val foregroundColor: Int,
    @Px private val marginLeft: Int = 0,
    @Px private val marginRight: Int = 0,
    @ColorInt private val backgroundColor: Int? = null
) : SimpleDivider(height, foregroundColor, marginLeft, marginRight, backgroundColor) {
    override fun needDraw(parent: RecyclerView, position: Int, itemsCount: Int): Boolean {
        return super.needDraw(parent, position, itemsCount) && parent.findViewHolderForLayoutPosition(position)?.itemViewType == holderType
    }

    override fun needAddOffsets(parent: RecyclerView, position: Int): Boolean {
        return super.needAddOffsets(parent, position) && parent.findViewHolderForLayoutPosition(position)?.itemViewType == holderType
    }
}
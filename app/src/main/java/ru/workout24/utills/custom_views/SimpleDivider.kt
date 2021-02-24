package ru.workout24.utills.custom_views

import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

open class SimpleDivider(
    @Px private val height: Int,
    @ColorInt private val foregroundColor: Int,
    @Px private val marginLeft: Int = 0,
    @Px private val marginRight: Int = 0,
    @ColorInt private val backgroundColor: Int? = null,
    @Px private val topMargin: Int = 0,
    @Px private val bottomMargin: Int = 0
): AbstractDivider(height, foregroundColor, marginLeft, marginRight, backgroundColor, topMargin, bottomMargin) {
    override fun needDraw(parent: RecyclerView, position: Int, itemsCount: Int): Boolean = position != itemsCount - 1

    override fun needAddOffsets(parent: RecyclerView, position: Int): Boolean = position != parent.childCount - 1
}
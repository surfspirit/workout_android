package ru.workout24.utills.custom_views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractDivider(
    @Px private val height: Int,
    @ColorInt private val foregroundColor: Int,
    @Px private val marginLeft: Int,
    @Px private val marginRight: Int,
    @ColorInt private val backgroundColor: Int? = null,
    @Px private val topMargin: Int = 0,
    @Px private val bottomMargin: Int = 0
): RecyclerView.ItemDecoration() {
    private val rect = Rect()
    private val foregroundPaint = Paint()
    private val backgroundPaint = Paint()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        foregroundPaint.color = foregroundColor
        backgroundPaint.color = backgroundColor ?: Color.TRANSPARENT
        var left = marginLeft
        var right = -marginRight
        if (parent.clipToPadding) {
            left += parent.paddingLeft
            right += parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            right += parent.width
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (needDraw(parent, i, childCount)) {
                val child = parent.getChildAt(i)
                parent.getDecoratedBoundsWithMargins(child, rect)
                val bottom = rect.bottom + child.translationY
                val top = bottom - height
//                canvas.drawRect(0f, top, parent.width.toFloat(), bottom, backgroundPaint)
                canvas.drawRect(left.toFloat(), top, right.toFloat(), bottom, foregroundPaint)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        if (needAddOffsets(parent, itemPosition)) {
            outRect.set(0, topMargin, 0, height + bottomMargin)
            return
        }
        outRect.set(0, 0, 0, height)
    }

    abstract fun needDraw(parent: RecyclerView, position: Int, itemsCount: Int): Boolean

    abstract fun needAddOffsets(parent: RecyclerView, position: Int): Boolean
}
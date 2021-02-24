package ru.workout24.ui.workout_diary.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ru.workout24.R

class CalendarBackgroundView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : View(context, attr) {
    private val dividerColor = ContextCompat.getColor(context, R.color.whiteThree)
    private val bottomRectColor = ContextCompat.getColor(context, R.color.coral)
    private val dividerHeight = context.resources.getDimensionPixelSize(R.dimen.divider_height)
    private val bottomRectHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_bottom_rect_height)
    private val paint = Paint()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            val bottomY = height.toFloat()
            paint.color = dividerColor
            drawRect(0f, (height - dividerHeight).toFloat(), width.toFloat(), bottomY, paint)
            paint.color = bottomRectColor
            val rectWidth = width / 7
            val center = width / 2
            drawRect((center - rectWidth).toFloat(), (height - bottomRectHeight).toFloat(), (center + rectWidth).toFloat(), bottomY, paint)
        }
    }
}
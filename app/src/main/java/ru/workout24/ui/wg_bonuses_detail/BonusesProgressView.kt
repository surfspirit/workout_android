package ru.workout24.ui.wg_bonuses_detail

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import ru.workout24.R

class BonusesProgressView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    View(context, attr) {
    private val PROGRESS_DURATION = 1500L
    private val progressAnimator by lazy {
        val animator = ValueAnimator()
        animator.duration = PROGRESS_DURATION
        animator.interpolator = FastOutSlowInInterpolator()
        animator.addUpdateListener {
            progressWidth = it.animatedValue as Float
            invalidate()
        }
        animator
    }
    private val progressHeight =
        context.resources.getDimensionPixelSize(R.dimen.progress_line_height).toFloat()
    private var progressWidth = 0f
    private val progressCornerRadius = progressHeight / 2
    private val backgroundPaint by lazy {
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.gunmetal)
        paint
    }
    private val foregroundPaint by lazy {
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.tomato)
        paint
    }

    private var currentCount: Int = 0
    private var totalCount: Int = 0

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        progressAnimator.setFloatValues(0f, ((width / totalCount) * currentCount).toFloat())
        progressAnimator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawRoundRect(
                0f,
                0f,
                width.toFloat(),
                progressHeight,
                progressCornerRadius,
                progressCornerRadius,
                backgroundPaint
            )
            drawRoundRect(
                0f,
                0f,
                progressWidth,
                progressHeight,
                progressCornerRadius,
                progressCornerRadius,
                foregroundPaint
            )
        }
    }

    fun setProgressCount(currentCount: Int, totalCount: Int) {
        this.currentCount = currentCount
        this.totalCount = totalCount
    }
}
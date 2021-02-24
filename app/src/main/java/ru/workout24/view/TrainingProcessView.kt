package ru.workout24.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import ru.workout24.R
import ru.workout24.utills.dpToPx
import java.lang.IllegalArgumentException


class TrainingProcessView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : LinearLayout(context, attributeSet) {
    private val circleWidth = 16.dpToPx(context)
    private val circleHeight = 16.dpToPx(context)

    var all = 0
    var current = 0

    private val redCircle get() = ImageView(context).apply {
        setImageResource(R.drawable.red_circle)
        layoutParams = circleParams
    }

    private val whiteCircle get() = ImageView(context).apply {
        setImageResource(R.drawable.white_circle_progress)
        layoutParams = circleParams
    }

    private val redLine get() = ImageView(context).apply {
        setImageResource(R.drawable.red_line)
        layoutParams = lineParams
    }

    private val gradientLine get() = ImageView(context).apply {
        setImageResource(R.drawable.red_white_gradient_line)
        layoutParams = lineParams
    }

    private val whiteLine get() = ImageView(context).apply {
        setImageResource(R.drawable.white_line)
        layoutParams = lineParams
    }

    private val circleParams by lazy {
        LayoutParams(
            circleWidth,
            circleHeight
        )
    }

    private val lineParams by lazy {
        LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
            1f
        )
    }

    init {
        orientation = HORIZONTAL
    }

    fun drawProgress() {
        if (all <= 0) throw IllegalArgumentException("Сount and current should be >0!")
        if (current > all) throw IllegalArgumentException("Current should be <= then count")
//        removeAllViews()
        if (current > 0) {
            addView(redCircle)
        } else {
            addView(whiteCircle)
        }
        //рисуем по количеству заданных кругов
        for (i in 1 until all) {
            when {
                current == 0 -> {
                    addView(whiteLine)
                }
                i < current -> {
                    addView(redLine)
                }
                i == current -> {
                    addView(gradientLine)
                }
                else -> {
                    addView(whiteLine)
                }
            }
            //если не дошли до текущего, то красное, иначе белое
            if (i < current) {
                addView(redCircle)

            } else {
                addView(whiteCircle)
            }
        }
        requestLayout()
    }

    fun next() {
        current++
        requestLayout()
    }
}